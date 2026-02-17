package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private String fonction;
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
    private String devise;
    private boolean actif; // ✅ doit correspondre exactement au nom utilisé dans le repository



    
    @OneToMany(mappedBy = "gardien", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pointage> pointages;

    @OneToMany(mappedBy = "gardien", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AgentFinanceHistory> agentFinanceHistory;

}
