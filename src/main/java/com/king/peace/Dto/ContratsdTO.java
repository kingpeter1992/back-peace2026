package com.king.peace.Dto;

import java.time.LocalDate;

import com.king.peace.Entitys.Devise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratsdTO {

    private Long id;
    private String refContrats;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montant;
    private int frequence; // nombre de jours/semaine ou mois
    private String typeService; // ex: surveillance jour/nuit
    private int nombreGardiens; // nombre de gardiens prévus
    private double montantParGardien; // montant que doit payer le client par gardien
    private int nombreJoursMensuel; // nombre de jours travaillés par mois
    private Devise devise; // USD ou FC
    private String activiteClient;
    private String typePaiement; // ex: mensuel, à la fin du service, etc.
    private LocalDate dateDebutFacturation; // date à partir de laquelle on commence à compter les jours pour la facturation
    private LocalDate dateEmission;
    private String statut;
    private String zone;
    private Long clientId;
    private String clientNom;
    private boolean active;
    
}
