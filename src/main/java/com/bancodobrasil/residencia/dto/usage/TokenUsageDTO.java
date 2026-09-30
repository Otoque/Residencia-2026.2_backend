package com.bancodobrasil.residencia.dto.usage;

public record TokenUsageDTO(
        int promptTokens,
        int completionTokens,
        int totalTokens
) {}
