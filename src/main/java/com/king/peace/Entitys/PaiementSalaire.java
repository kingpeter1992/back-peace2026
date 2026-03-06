package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.king.peace.enums.StatutPaie;

@Entity
@Table(name = "paiements_salaire",
       uniqueConstraints = @UniqueConstraint(columnNames = {"gardien_id", "mois", "annee"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaiementSalaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardien_id", nullable = false)
    private Gardien gardien;

    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    private double salaireBase;

    private double totalPrimes;

    private double totalRetenues;

    private double totalAvances;

    private double totalRemboursementPret;

    private double salaireBrut;

    private double salaireNet;

    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaie statut;

        @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

}
