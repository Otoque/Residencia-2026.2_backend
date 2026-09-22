package com.bancodobrasil.residencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentalImpactDTO {
  private int totalTokens;
  private double energyConsumedWh;
  private double carbonFootprintGrams;
  private double waterConsumedMl;
}
