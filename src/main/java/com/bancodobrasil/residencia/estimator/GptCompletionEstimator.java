package com.bancodobrasil.residencia.estimator;

import org.springframework.stereotype.Component;

@Component
public class GptCompletionEstimator implements CompletionTokenEstimator {

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gpt");
    }

    @Override
    public int estimateCompletionTokens(String prompt, int promptTokens) {
        String lower = prompt.toLowerCase();

        // Heurística baseada em palavras-chave comuns no prompt.
        // Ajuste esses multiplicadores conforme for coletando dados reais.
        if (contemAlguma(lower, "resuma", "liste", "sim ou não", "responda em uma palavra")) {
            return (int) Math.ceil(promptTokens * 0.5) + 20;
        }

        if (contemAlguma(lower, "explique", "detalhe", "descreva", "análise completa")) {
            return (int) Math.ceil(promptTokens * 2.0) + 100;
        }

        if (contemAlguma(lower, "código", "function", "classe", "implemente", "script")) {
            return (int) Math.ceil(promptTokens * 2.5) + 150;
        }

        // Caso padrão
        return (int) Math.ceil(promptTokens * 1.3) + 50;
    }

    private boolean contemAlguma(String texto, String... termos) {
        for (String termo : termos) {
            if (texto.contains(termo)) return true;
        }
        return false;
    }
}
