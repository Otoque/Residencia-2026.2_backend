package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;

public interface ModelImpactStrategy {
    boolean supports(String modelName);
    EnvironmentalImpactDTO calculateImpact(TokenUsageDTO usage);
}
