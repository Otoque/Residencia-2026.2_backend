package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.dto.response.LoginResponseDTO;
import com.bancodobrasil.residencia.dto.response.RegisterResponseDTO;
import com.bancodobrasil.residencia.model.User;
import com.bancodobrasil.residencia.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private static final int BCRYPT_MAX_BYTES = 72;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final String USERNAME_PATTERN = "^[a-z0-9._-]{3,30}$";

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService attemptService;
    private final UserRepository userRepository;
    private final String dummyHash;

    public AuthService(PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       LoginAttemptService attemptService,
                       UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.attemptService = attemptService;
        this.userRepository = userRepository;
        // usado quando o usuário não existe, para o tempo de resposta ser igual ao de um usuário real
        this.dummyHash = passwordEncoder.encode("senha-invalida-apenas-para-igualar-tempo");
    }

    public RegisterResponseDTO register(String username, String password) {
        String user = normalize(username);

        if (!user.matches(USERNAME_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Usuário deve ter de 3 a 30 caracteres: letras, números, ponto, hífen ou underline");
        }
        if (password == null || password.length() < PASSWORD_MIN_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A senha deve ter no mínimo " + PASSWORD_MIN_LENGTH + " caracteres");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A senha deve ter no máximo " + BCRYPT_MAX_BYTES + " bytes");
        }
        if (userRepository.existsByUsername(user)) {
            throw usernameTaken();
        }

        User entity = new User();
        entity.setUsername(user);
        entity.setPasswordHash(passwordEncoder.encode(password));

        try {
            userRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            // duas requisições simultâneas com o mesmo nome
            throw usernameTaken();
        }

        return new RegisterResponseDTO(user);
    }

    public LoginResponseDTO login(String username, String password, String clientIp) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário e senha são obrigatórios");
        }

        String user = normalize(username);
        String key = clientIp + "|" + user;

        if (attemptService.isBlocked(key)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas. Tente novamente mais tarde.");
        }

        Optional<User> found = userRepository.findByUsername(user);
        String hash = found.map(User::getPasswordHash).orElse(dummyHash);

        // Sempre roda o BCrypt (mesmo sem usuário) para não revelar por tempo de resposta se a conta existe.
        boolean passOk = password.getBytes(StandardCharsets.UTF_8).length <= BCRYPT_MAX_BYTES
                && passwordEncoder.matches(password, hash);

        if (found.isEmpty() || !passOk) {
            attemptService.recordFailure(key);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }

        attemptService.reset(key);
        return jwtService.generate(found.get().getUsername());
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private ResponseStatusException usernameTaken() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Esse nome de usuário já está em uso");
    }
}
