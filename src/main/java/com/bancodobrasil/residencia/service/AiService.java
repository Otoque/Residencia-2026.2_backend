package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.model.ChatRequest;
import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiService {

  @Autowired
  private EnvironmentalImpactService impactService;

  public EnvironmentalImpactDTO simulateImpact(ChatRequest request) {
    String promptText = "";
    if (request.getMessages() != null && !request.getMessages().isEmpty()) {
      promptText = request.getMessages().get(request.getMessages().size() - 1).getContent();
    }

    String modelName = request.getModel() != null ? request.getModel() : "gemini";

    return impactService.estimateFromPrompt(modelName, promptText);
  }
}
