package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;
import com.bancodobrasil.residencia.estimator.CompletionTokenEstimator;
import com.bancodobrasil.residencia.tokenizer.Tokenizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenEstimationService {

    private final List<Tokenizer> tokenizers;
    private final List<CompletionTokenEstimator> estimators;

    public TokenEstimationService(List<Tokenizer> tokenizers,
                                  List<CompletionTokenEstimator> estimators) {
        this.tokenizers = tokenizers;
        this.estimators = estimators;
    }

    public TokenUsageDTO estimate(
            String modelName,
            String prompt,
            Integer providedPromptTokens,
            Integer providedCompletionTokens,
            String completionText
    ) {
        int promptTokens = resolvePromptTokens(modelName, prompt, providedPromptTokens);

        int completionTokens = resolveCompletionTokens(
                modelName,
                prompt,
                promptTokens,
                providedCompletionTokens,
                completionText
        );

        int totalTokens = promptTokens + completionTokens;

        return new TokenUsageDTO(promptTokens, completionTokens, totalTokens);
    }

    private int resolvePromptTokens(
            String modelName,
            String prompt,
            Integer providedPromptTokens
    ) {
        if (providedPromptTokens != null) {
            validateNonNegative("promptTokens", providedPromptTokens);
            return providedPromptTokens;
        }

        if (prompt == null || prompt.isBlank()) {
            return 0;
        }

        return countTextTokens(modelName, prompt);
    }

    private int resolveCompletionTokens(
            String modelName,
            String prompt,
            int promptTokens,
            Integer providedCompletionTokens,
            String completionText
    ) {
        // 1. Se o usuário mandou o número de tokens de saída, usa direto
        if (providedCompletionTokens != null) {
            validateNonNegative("completionTokens", providedCompletionTokens);
            return providedCompletionTokens;
        }

        // 2. Se o usuário mandou o texto da resposta da IA, conta os tokens desse texto
        if (completionText != null && !completionText.isBlank()) {
            return countTextTokens(modelName, completionText);
        }

        // 3. Se não veio nem número nem texto, cai na estimativa antiga
        return estimators.stream()
                .filter(e -> e.supports(modelName))
                .findFirst()
                .map(e -> e.estimateCompletionTokens(prompt, promptTokens))
                .orElseGet(() -> (int) Math.ceil(promptTokens * 1.2));
    }

    private int countTextTokens(String modelName, String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        return tokenizers.stream()
                .filter(t -> t.supports(modelName))
                .findFirst()
                .map(t -> t.countTokens(modelName, text))
                .orElseGet(() -> fallbackTokenCount(text));
    }

    private int fallbackTokenCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        return (int) Math.ceil(text.length() / 4.0);
    }

    private void validateNonNegative(String fieldName, Integer value) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo");
        }
    }
}
