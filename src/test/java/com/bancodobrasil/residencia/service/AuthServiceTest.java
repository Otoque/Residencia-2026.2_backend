package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.config.SecurityConfig;
import com.bancodobrasil.residencia.dto.response.LoginResponseDTO;
import com.bancodobrasil.residencia.model.User;
import com.bancodobrasil.residencia.repository.UserRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final String IP = "127.0.0.1";

    private final SecretKey key = new SecretKeySpec(
            "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    private final Map<String, User> banco = new HashMap<>();
    private AuthService authService;

    @BeforeEach
    void setUp() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByUsername(anyString()))
                .thenAnswer(i -> Optional.ofNullable(banco.get(i.<String>getArgument(0))));
        when(repository.existsByUsername(anyString()))
                .thenAnswer(i -> banco.containsKey(i.<String>getArgument(0)));
        when(repository.saveAndFlush(any(User.class)))
                .thenAnswer(i -> {
                    User u = i.getArgument(0);
                    banco.put(u.getUsername(), u);
                    return u;
                });

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        JwtService jwtService = new JwtService(new NimbusJwtEncoder(new ImmutableSecret<>(key)), 60);
        authService = new AuthService(encoder, jwtService, new LoginAttemptService(3, 15), repository);
    }

    @Test
    void cadastraELogaComTokenValido() {
        authService.register("Maria", "senha-forte-123");
        LoginResponseDTO response = authService.login("maria", "senha-forte-123", IP);

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(SecurityConfig.JWT_ISSUER));
        Jwt jwt = decoder.decode(response.accessToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(jwt.getSubject()).isEqualTo("maria");
    }

    @Test
    void senhaFicaSalvaComoHashBcrypt() {
        authService.register("joao", "senha-forte-123");

        assertThat(banco.get("joao").getPasswordHash())
                .startsWith("$2")
                .doesNotContain("senha-forte-123");
    }

    @Test
    void naoPermiteNomeDuplicado() {
        authService.register("joao", "senha-forte-123");

        assertStatus(() -> authService.register("JOAO", "outra-senha-123"), HttpStatus.CONFLICT);
    }

    @Test
    void rejeitaSenhaCurtaEUsuarioInvalido() {
        assertStatus(() -> authService.register("joao", "123"), HttpStatus.BAD_REQUEST);
        assertStatus(() -> authService.register("a b", "senha-forte-123"), HttpStatus.BAD_REQUEST);
    }

    @Test
    void senhaErradaEUsuarioInexistenteDaoOMesmo401() {
        authService.register("joao", "senha-forte-123");

        assertStatus(() -> authService.login("joao", "errada", IP), HttpStatus.UNAUTHORIZED);
        assertStatus(() -> authService.login("fantasma", "senha-forte-123", IP), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void bloqueiaApos3FalhasMesmoComSenhaCorreta() {
        authService.register("joao", "senha-forte-123");
        for (int i = 0; i < 3; i++) {
            assertStatus(() -> authService.login("joao", "errada", IP), HttpStatus.UNAUTHORIZED);
        }

        assertStatus(() -> authService.login("joao", "senha-forte-123", IP), HttpStatus.TOO_MANY_REQUESTS);
    }

    private void assertStatus(Runnable action, HttpStatus expected) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(expected));
    }
}
