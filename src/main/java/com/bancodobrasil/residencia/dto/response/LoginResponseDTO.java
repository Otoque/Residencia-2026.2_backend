package com.bancodobrasil.residencia.dto.response;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
