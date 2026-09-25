package com.bancodobrasil.residencia.service;

import com.bancodobrasil.residencia.model.ChatRequest;
import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiService {

  @Autowired
  private EnvironmentalImpactService impactService;

  // Modificamos para retornar diretamente a estimativa (ou um DTO próprio de simulação)
  public EnvironmentalImpactDTO simulateImpact(ChatRequest request) {
    // 1. Extrai o texto do prompt de dentro da lista de mensagens usando o .getContent()
    String promptText = "";
    if (request.getMessages() != null && !request.getMessages().isEmpty()) {
      promptText = request.getMessages().get(request.getMessages().size() - 1).getContent();
    }

    // 2. Pega o modelo que veio na requisição (ou usa "gemini" por defeito)
    String modelName = request.getModel() != null ? request.getModel() : "gemini";

    // 3. Calcula o impacto preventivamente usando os agentes de estratégia
    return impactService.estimateFromPrompt(modelName, promptText);
  }
}
