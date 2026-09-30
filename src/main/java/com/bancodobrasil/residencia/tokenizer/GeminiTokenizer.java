package com.bancodobrasil.residencia.tokenizer;

import org.springframework.stereotype.Component;

@Component
public class GeminiTokenizer implements Tokenizer {

    // Google não publica uma lib Java offline confiável para o tokenizer deles.
    // Por isso usamos uma heurística calibrada para português.
    private static final double CHARS_PER_TOKEN = 3.0;

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gemini");
    }

    @Override
    public int countTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Math.ceil(text.length() / CHARS_PER_TOKEN);
    }
}
