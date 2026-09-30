package com.bancodobrasil.residencia.estimator;

import org.springframework.stereotype.Component;

@Component
public class GeminiCompletionEstimator implements CompletionTokenEstimator {

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gemini");
    }

    @Override
    public int estimateCompletionTokens(String prompt, int promptTokens) {
        String lower = prompt.toLowerCase();

        // Gemini tende a ser mais direto que o GPT em respostas médias.
        if (contemAlguma(lower, "resuma", "liste", "sim ou não")) {
            return (int) Math.ceil(promptTokens * 0.4) + 15;
        }

        if (contemAlguma(lower, "explique", "detalhe", "descreva")) {
            return (int) Math.ceil(promptTokens * 1.6) + 80;
        }

        if (contemAlguma(lower, "código", "function", "classe", "implemente")) {
            return (int) Math.ceil(promptTokens * 2.2) + 120;
        }

        return (int) Math.ceil(promptTokens * 1.1) + 40;
    }

    private boolean contemAlguma(String texto, String... termos) {
        for (String termo : termos) {
            if (texto.contains(termo)) return true;
        }
        return false;
    }
}
