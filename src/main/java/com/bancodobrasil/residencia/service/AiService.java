package com.bancodobrasil.residencia.service;

import org.springframework.web.client.HttpStatusCodeException;
import com.bancodobrasil.residencia.model.ChatRequest;

import tools.jackson.databind.ObjectMapper;

import com.bancodobrasil.residencia.dto.ChatResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

  @Value("${openai.api.key}")
  private String apiKey;

  private final String OPENAI_URL = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

  public ChatResponseDTO callChatGpt(ChatRequest request) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<String> responseString = restTemplate.exchange(
          OPENAI_URL,
          HttpMethod.POST,
          entity,
          String.class
      );

      System.out.println("Raw API Json: " + responseString.getBody());
      ObjectMapper objectMapper = new ObjectMapper();
      ChatResponseDTO responseDTO = objectMapper.readValue(responseString.getBody(), ChatResponseDTO.class);
      System.out.println("Response mapped: " + responseDTO);

      return responseDTO;

    } catch (HttpStatusCodeException e) {
      System.out.println("Error returned by the API (Body): " + e.getResponseBodyAsString());
      throw e;
    } catch (Exception e){
      throw new RuntimeException("Error converting AI response JSON");
    }
  }
}
