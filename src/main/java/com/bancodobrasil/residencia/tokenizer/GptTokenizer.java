package com.bancodobrasil.residencia.tokenizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.stereotype.Component;

@Component
public class GptTokenizer implements Tokenizer {

    private final Encoding encoding;

    public GptTokenizer() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncodingForModel(ModelType.GPT_4O);
    }

    @Override
    public boolean supports(String modelName) {
        return modelName != null && modelName.toLowerCase().contains("gpt");
    }

    @Override
    public int countTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return encoding.countTokens(text);
    }
}
