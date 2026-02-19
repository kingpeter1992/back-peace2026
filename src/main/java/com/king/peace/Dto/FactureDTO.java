package com.king.peace.Dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureDTO {
    private Long id;
    private LocalDate dateEmission;
    private double montantTotal;
    private String statut; // "NEW", "PAID", etc.
    private String description;
    private String refFacture;
    private int nombreGardiens;
    private double montantParGardien;
    private double nombreJours;
    private String devise;
    private Long clientId;
    private Long contratId;
        private Long factureOrigineId; // pour avoir
        private double remise;
    private String commentaire;

}
