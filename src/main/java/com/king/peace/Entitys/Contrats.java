package com.king.peace.Entitys;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Contrats {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @Column(unique = true)
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
   private LocalDate createdAt=LocalDate.now();


    @ManyToOne private Client client;


    @OneToMany(mappedBy = "contrats") 
    private List<Facture> factures;

      // Gardiens affectés via Affectation
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Affectation> affectations;
}
