package com.bancodobrasil.residencia.dto;

public record EnvironmentalImpactDTO(
        int totalTokens,
        double energyConsumedWh,
        double carbonFootprintGrams,
        double waterConsumedMl
) {}
