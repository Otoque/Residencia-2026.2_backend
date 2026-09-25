package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.service.agents.ModelImpactStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentalImpactService {

    @Autowired
    private List<ModelImpactStrategy> strategies;

    public EnvironmentalImpactDTO estimateFromPrompt(String modelName, String promptText) {
        ModelImpactStrategy strategy = strategies.stream()
                .filter(s -> s.supports(modelName))
                .findFirst()
                .orElseGet(() -> strategies.get(0));

        return strategy.calculateImpact(promptText);
    }
}
