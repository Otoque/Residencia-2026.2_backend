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

    public TokenUsageDTO estimate(String modelName, String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return new TokenUsageDTO(0, 0, 0);
        }

        int promptTokens = tokenizers.stream()
                .filter(t -> t.supports(modelName))
                .findFirst()
                .map(t -> t.countTokens(prompt))
                .orElseGet(() -> fallbackTokenCount(prompt));

        int completionTokens = estimators.stream()
                .filter(e -> e.supports(modelName))
                .findFirst()
                .map(e -> e.estimateCompletionTokens(prompt, promptTokens))
                .orElseGet(() -> (int) Math.ceil(promptTokens * 1.2));

        int totalTokens = promptTokens + completionTokens;

        return new TokenUsageDTO(promptTokens, completionTokens, totalTokens);
    }

    private int fallbackTokenCount(String prompt) {
        return (int) Math.ceil(prompt.length() / 4.0);
    }
}
