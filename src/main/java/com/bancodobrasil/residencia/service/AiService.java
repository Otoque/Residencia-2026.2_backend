package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import com.bancodobrasil.residencia.dto.response.ChatResponseDTO;
import com.bancodobrasil.residencia.dto.usage.TokenUsageDTO;
import com.bancodobrasil.residencia.model.ChatRequest;
import com.bancodobrasil.residencia.model.Message;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final TokenEstimationService tokenEstimationService;
    private final EnvironmentalImpactService impactService;

    public AiService(TokenEstimationService tokenEstimationService,
                     EnvironmentalImpactService impactService) {
        this.tokenEstimationService = tokenEstimationService;
        this.impactService = impactService;
    }

    public ChatResponseDTO estimate(ChatRequest request) {
        String prompt = extractPrompt(request);

        if (prompt.isBlank()) {
            throw new IllegalArgumentException("O prompt não pode ser vazio");
        }

        String model = request.getModel();
        TokenUsageDTO usage = tokenEstimationService.estimate(model, prompt);
        EnvironmentalImpactDTO impact = impactService.calculate(model, usage);

        return new ChatResponseDTO(model, usage, impact);
    }

    private String extractPrompt(ChatRequest request) {
        if (request.getMessages() == null) return "";

        return request.getMessages().stream()
                .map(Message::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }
}
