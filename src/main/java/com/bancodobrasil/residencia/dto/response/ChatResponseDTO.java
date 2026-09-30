package com.bancodobrasil.residencia.dto.response;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;

public record ChatResponseDTO(
        String model,
        TokenUsageDTO usage,
        EnvironmentalImpactDTO environmentalImpact
) {}
