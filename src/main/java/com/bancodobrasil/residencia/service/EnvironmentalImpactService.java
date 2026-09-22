package com.bancodobrasil.residencia.service;

import org.springframework.stereotype.Service;
import com.bancodobrasil.residencia.dto.EnvironmentalImpactDTO;


@Service
public class EnvironmentalImpactService{

  public EnvironmentalImpactDTO calculate(Integer totalTokens){
    if (totalTokens == null || totalTokens <= 0){
      return new EnvironmentalImpactDTO(0, 0.0, 0.0, 0.0);
    }
    
    double energyWh = totalTokens * 0.00003;
    double carbonGrams = totalTokens * 0.00043;
    double waterMl = totalTokens * 0.002;

    return new EnvironmentalImpactDTO(totalTokens, energyWh, carbonGrams, waterMl);
  }
}
