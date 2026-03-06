package com.king.peace.Dto;

import lombok.*;
import java.time.LocalDate;

import com.king.peace.enums.StatutPaie;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaiementSalaireDTO {
    private Long id;
    private Long employeId;
    private String employeNom;
    private Integer mois;
    private Integer annee;
    private double salaireBase;
    private double totalPrimes;
    private double totalRetenues;
    private double totalAvances;
    private double totalRemboursementPret;
    private double salaireBrut;
    private double salaireNet;
    private LocalDate datePaiement;
    private StatutPaie statut;
}