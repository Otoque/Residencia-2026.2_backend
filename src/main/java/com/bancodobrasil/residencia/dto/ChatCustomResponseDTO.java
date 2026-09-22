package com.bancodobrasil.residencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCustomResponseDTO{
  private ChatResponseDTO aiResponse;
  private EnvironmentalImpactDTO environmentalImpact;
}
