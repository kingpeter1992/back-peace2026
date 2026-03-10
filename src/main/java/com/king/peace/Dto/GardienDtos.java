package com.king.peace.Dto;

import java.time.LocalDate;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.StatutGardien;
import com.king.peace.enums.Departement;
import com.king.peace.enums.Fonction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Setter @Getter
public class GardienDtos {
        private Long id;
    private String nom;
    private String prenom;
    private String telephone1;
    private String telephone2;
    private Fonction fonction;
    private Departement departement;
    private String site;
    private double salaire;
    private String adresse;
    private String genre;
    private double salaireBase;
//    private double bonus;
    private StatutGardien statut;
    private LocalDate dateEmbauche;
    private String email;
    private LocalDate dateNaissance;
    private LocalDate createdAt;
    private Devise devise;
    private Integer  nbrjours;
}
