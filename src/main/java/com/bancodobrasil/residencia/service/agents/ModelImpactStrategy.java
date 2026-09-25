package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;

public interface ModelImpactStrategy {
    EnvironmentalImpactDTO calculateImpact(String promptText);
    boolean validator(String modelName);
}
