package com.bancodobrasil.residencia.service.agents;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import org.springframework.stereotype.Component;

@Component
public class GptImpactStrategy implements ModelImpactStrategy {

    @Override
    public boolean supports(String modelName) {
        // Se o nome do modelo enviado no JSON contiver "gpt", este agente assume o cálculo
        return modelName != null && modelName.toLowerCase().contains("gpt");
    }

    @Override
    public EnvironmentalImpactDTO calculateImpact(String promptText) {
        if (promptText == null || promptText.isEmpty()) {
            return new EnvironmentalImpactDTO(0, 0.0, 0.0, 0.0);
        }

        // Estimativa de tokens do prompt (ex: proporção média diferente para modelos GPT se desejar)
        int estimatedPromptTokens = (int) Math.ceil(promptText.length() / 4.0);
        int estimatedCompletionTokens = 150; // Média estimada de resposta
        int totalEstimatedTokens = estimatedPromptTokens + estimatedCompletionTokens;

        // Fatores de impacto ecológico específicos simulados para o GPT
        double energyWh = totalEstimatedTokens * 0.000035; 
        double carbonGrams = energyWh * 0.00048; 
        double waterMl = totalEstimatedTokens * 0.0022;

        return new EnvironmentalImpactDTO(totalEstimatedTokens, energyWh, carbonGrams, waterMl);
    }
}
