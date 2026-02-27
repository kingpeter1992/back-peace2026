package com.king.peace.Dto;

import java.time.LocalDate;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.StatutFacture;

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
    LocalDate dateCycle;
    private String refFacture;
    private int nombreGardiens;
    private double montantParGardien;
    private double nombreJours;
    private String devise;
    private Long clientId;
    private Long contratId;
  private String motifAvoir;
        private Long factureOrigineId; // pour avoir
        private double remise;
    private String commentaire;
    private Double montantPaye = 0.0;
    private StatutFacture status;
    private Devise  devis;


}
