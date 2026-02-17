package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dates de début et fin de l'affectation
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;

    // Gardien affecté
    @ManyToOne
    @JoinColumn(name = "gardien_id")
    private Gardien gardien;

    // Contrat lié
    @ManyToOne
    @JoinColumn(name = "contrats_id")
    private Contrats contrat;

    // Statut de l'affectation
    @Enumerated(EnumType.STRING)
    private StatutAffectation statut;

    private LocalDate dateAffectation;
    private String  description;
}
