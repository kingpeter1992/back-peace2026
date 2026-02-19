package com.king.peace.Entitys;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cglib.core.Local;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Plaintes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate datePlainte;
    private String description;
    private int note; // 0 à 10
    @Enumerated(EnumType.STRING)
    private NiveauPlainte niveau;
    private boolean repondu = false;
    private LocalDate dateLimiteReponse;
    private String response;
    private LocalDate dateRespnse;
    private String statut;
    private LocalDate createdAt;
    private String reponseGardien;
    private boolean active = true;



    @ManyToOne
    @JoinColumn(name = "gardien_id") // la colonne dans la table
    private Gardien gardien; // ✅ c’est CE nom que Spring va utiliser

    @ManyToOne
    private Client client;

      // ✅ RELATION CORRECTE POUR LES REPONSES
  @OneToMany(
            mappedBy = "plainte",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ReponsePlainte> reponses = new ArrayList<>();
}
