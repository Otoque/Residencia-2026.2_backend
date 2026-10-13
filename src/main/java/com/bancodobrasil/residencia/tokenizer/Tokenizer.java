package com.bancodobrasil.residencia.tokenizer;

public interface Tokenizer {
    boolean supports(String modelName);
    int countTokens(String modelName, String text);
}
