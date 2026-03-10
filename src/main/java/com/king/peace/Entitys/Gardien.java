package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.king.peace.enums.Departement;
import com.king.peace.enums.Fonction;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Gardien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String telephone1;
    private String telephone2;
    private double salaire;
    private String adresse;
    private String genre;
    private double salaireBase;
    // private double bonus;
    private StatutGardien statut;
    private LocalDate dateEmbauche;
    private String email;
    private LocalDate dateNaissance;
    private LocalDate createdAt;
    private Devise devise;
    private boolean actif; // ✅ doit correspondre exactement au nom utilisé dans le repository
    private Departement departement;
    private Fonction fonction;
    private Integer  nbrjours;
    private String site;



    
    @OneToMany(mappedBy = "gardien", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pointage> pointages;

    @OneToMany(mappedBy = "gardien", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AgentFinanceHistory> agentFinanceHistory;

}
