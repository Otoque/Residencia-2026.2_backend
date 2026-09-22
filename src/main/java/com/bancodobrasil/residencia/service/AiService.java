package com.bancodobrasil.residencia.service;

import org.springframework.web.client.HttpStatusCodeException;
import com.bancodobrasil.residencia.model.ChatRequest;

import tools.jackson.databind.ObjectMapper;

import com.bancodobrasil.residencia.dto.ChatCustomResponseDTO;
import com.bancodobrasil.residencia.dto.ChatResponseDTO;
import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {
  @Autowired
  private EnvironmentalImpactService impactService;

  @Value("${openai.api.key}")
  private String apiKey;

  private final String Ai_URL = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

  public ChatCustomResponseDTO callChatGpt(ChatRequest request) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<String> responseString = restTemplate.exchange(
          Ai_URL,
          HttpMethod.POST,
          entity,
          String.class
      );

      ObjectMapper objectMapper = new ObjectMapper();
      ChatResponseDTO responseDTO = objectMapper.readValue(responseString.getBody(), ChatResponseDTO.class);

      Integer totalTokens = 0;
      if (responseDTO.getUsage() != null && responseDTO.getUsage().getTotal_tokens() != null){
        totalTokens = responseDTO.getUsage().getTotal_tokens();
      }

      EnvironmentalImpactDTO impactDTO = impactService.calculate(totalTokens);

      return new ChatCustomResponseDTO(responseDTO,impactDTO);
    } catch (HttpStatusCodeException e) {
      System.out.println("Error returned by the API (Body): " + e.getResponseBodyAsString());
      throw e;
    } catch (Exception e){
      throw new RuntimeException("Error converting AI response JSON");
    }
  }
}
