package com.king.peace.Dto.Response;

import java.time.LocalDate;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.StatutFacture;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class FactureMiniDto {
  
  private Long id;
  private String reference;
private Devise devise;
  private Long clientId;
  private LocalDate dateEmission;
  private double montantTotal;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private String description;
  private String refFacture;
  private double reste;
  private double montantPaye;
  private int nombreGardiens;
  private double totalFactures;
  private double montantParGardien; // <-- attention au nom exact !
  private double nombreJours;
  private double remise = 0.0; // remise appliquée
  private String commentaire; // notes ou commentaire utilisateur
  private String motifAvoir;
  private double totalNet;
  private StatutFacture statut;

}