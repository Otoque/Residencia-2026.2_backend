package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import org.springframework.stereotype.Component;

@Component
public class GeminiImpactStrategy implements ModelImpactStrategy {

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gemini");
    }

    @Override
    public EnvironmentalImpactDTO calculateImpact(String promptText) {
        if (promptText == null || promptText.isEmpty()) {
            return new EnvironmentalImpactDTO(0, 0.0, 0.0, 0.0);
        }

        int estimatedPromptTokens = (int) Math.ceil(promptText.length() / 3.5);
        int estimatedCompletionTokens = 150; 
        int totalEstimatedTokens = estimatedPromptTokens + estimatedCompletionTokens;

        double energyWh = totalEstimatedTokens * 0.00003; 
        double carbonGrams = energyWh * 0.00043; 
        double waterMl = totalEstimatedTokens * 0.002;

        return new EnvironmentalImpactDTO(totalEstimatedTokens, energyWh, carbonGrams, waterMl);
    }
}
