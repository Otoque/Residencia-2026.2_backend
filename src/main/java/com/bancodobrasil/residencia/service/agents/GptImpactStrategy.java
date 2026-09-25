package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import org.springframework.stereotype.Component;

@Component
public class GptImpactStrategy implements ModelImpactStrategy {

    @Override
    public boolean validator(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gpt");
    }

    @Override
    public EnvironmentalImpactDTO calculateImpact(String promptText) {
        if (promptText == null || promptText.isEmpty()) {
            return new EnvironmentalImpactDTO(0, 0.0, 0.0, 0.0);
        }

        int estimatedPromptTokens = (int) Math.ceil(promptText.length() / 4.0);
        int estimatedCompletionTokens = 150;
        int totalEstimatedTokens = estimatedPromptTokens + estimatedCompletionTokens;

        double energyWh = totalEstimatedTokens * 0.000035; 
        double carbonGrams = energyWh * 0.00048; 
        double waterMl = totalEstimatedTokens * 0.0022;

        return new EnvironmentalImpactDTO(totalEstimatedTokens, energyWh, carbonGrams, waterMl);
    }
}
