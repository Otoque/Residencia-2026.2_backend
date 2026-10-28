package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.config.SecurityConfig;
import com.bancodobrasil.residencia.dto.response.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final Duration expiration;

    public JwtService(JwtEncoder encoder,
                      @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public LoginResponseDTO generate(String username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(SecurityConfig.JWT_ISSUER)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plus(expiration))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new LoginResponseDTO(token, "Bearer", expiration.toSeconds());
    }
}
