package com.king.peace.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class PaiementDashboardDTO {
       private long totalEmployes;

    private double salaireBaseUsd;
    private double salaireBaseCdf;

    private double totalPrimesUsd;
    private double totalPrimesCdf;

    private double totalAvancesUsd;
    private double totalAvancesCdf;

    private double totalPretsUsd;
    private double totalPretsCdf;

    private double totalRetenuesUsd;
    private double totalRetenuesCdf;

    private double netAPayerUsd;
    private double netAPayerCdf;
}
