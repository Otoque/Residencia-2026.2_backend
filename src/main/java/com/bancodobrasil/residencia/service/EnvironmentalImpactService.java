package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;
import com.bancodobrasil.residencia.service.agents.ModelImpactStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentalImpactService {

    private final List<ModelImpactStrategy> strategies;

    public EnvironmentalImpactService(List<ModelImpactStrategy> strategies) {
        this.strategies = strategies;
    }

    public EnvironmentalImpactDTO calculate(String modelName, TokenUsageDTO usage) {
        return strategies.stream()
                .filter(s -> s.supports(modelName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Modelo não suportado: " + modelName))
                .calculateImpact(usage);
    }
}
