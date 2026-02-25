package com.king.peace.Dto.Response;

import java.time.LocalDate;

import com.king.peace.Dto.ClientDto;
import com.king.peace.Entitys.Devise;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ContratDetailsMiniDto {
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
    @Enumerated(EnumType.STRING)
    private Devise devise; // USD ou FC
    private String activiteClient;
    private String typePaiement; // ex: mensuel, à la fin du service, etc.
    private LocalDate dateDebutFacturation; // date à partir de laquelle on commence à compter les jours pour la facturation
    private LocalDate dateEmission;
    private String zone;
    private String statut; // EX: EMIS, ANNULÉ, REFACTURE
   private boolean active=true;
  private ClientDto client;
  private java.util.List<GardienMiniDto> gardiens;
}