package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "avances_salaire")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvanceSalaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardien_id", nullable = false)
    private Gardien gardien;

    private double montant;

    @Column(nullable = false)
    private LocalDate dateAvance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.king.peace.enums.StatutAvance statut;
    private String motif;

    private String observation;

           @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

      @Column(name = "mois_concerne", nullable = false)
    private Integer moisConcerne;

    @Column(name = "annee_concerne", nullable = false)
    private Integer anneeConcerne;

    
}