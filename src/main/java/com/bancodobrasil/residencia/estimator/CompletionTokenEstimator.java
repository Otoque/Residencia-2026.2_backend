package com.bancodobrasil.residencia.estimator;

public interface CompletionTokenEstimator {
    boolean supports(String modelName);
    int estimateCompletionTokens(String prompt, int promptTokens);
}
