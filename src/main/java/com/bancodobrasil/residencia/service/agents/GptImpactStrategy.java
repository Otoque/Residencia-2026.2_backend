package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;
import org.springframework.stereotype.Component;

@Component
public class GptImpactStrategy implements ModelImpactStrategy {

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gpt");
    }

    @Override
    public EnvironmentalImpactDTO calculateImpact(TokenUsageDTO usage) {
        int total = usage.totalTokens();

        // Troque pelos coeficientes do seu arquivo antigo de GPT
        double energyWh = total * 0.00004;
        double carbonGrams = energyWh * 0.00045;
        double waterMl = total * 0.0022;

        return new EnvironmentalImpactDTO(total, energyWh, carbonGrams, waterMl);
    }
}
