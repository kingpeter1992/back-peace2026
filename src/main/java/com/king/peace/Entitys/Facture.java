package com.king.peace.Entitys;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private LocalDate dateEmission;
  private double montantTotal;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private StatutFacture statut;
  private String description;
  private String refFacture;
  private int nombreGardiens;
  private double montantParGardien; // <-- attention au nom exact !
  private double nombreJours;
  private Devise devise;
  private double remise = 0.0; // remise appliquée
  private String commentaire; // notes ou commentaire utilisateur
  private String motifAvoir;
private double montantPaye  ;
@Transient
public double getReste() {
    double totalNet = montantTotal - (remise > 0 ? remise : 0);
    return Math.max(0, totalNet - montantPaye);
}


  @ManyToOne
  private Client client;
  @ManyToOne
  @JoinColumn(name = "contrats_id")
  private Contrats contrats;
  private Integer heureFacturation;
  private LocalDate dateCycle;
  @OneToOne
  @JoinColumn(name = "facture_origine_id")
  private Facture factureOrigine; // pour les avoirs
}
