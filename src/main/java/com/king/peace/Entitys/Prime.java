package com.king.peace.Entitys;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.king.peace.enums.StatutPrime;
import com.king.peace.enums.TypePrime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "primes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prime {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardien_id", nullable = false)
    private Gardien gardien;

    @Column(nullable = false)
    private Double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

    @Column(name = "date_prime", nullable = false)
    private LocalDate datePrime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_prime", nullable = false)
    private TypePrime typePrime;

    @Column(nullable = false, length = 500)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPrime statut;

    @Column(name = "mois_concerne", nullable = false)
    private Integer moisConcerne;

    @Column(name = "annee_concerne", nullable = false)
    private Integer anneeConcerne;

    @Column(length = 1000)
    private String observation;

}