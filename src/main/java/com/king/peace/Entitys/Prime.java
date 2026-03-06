package com.king.peace.Entitys;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
    private TypePrime typePrime;

    @Column(nullable = false)
    private String libelle;

    private double montant;

    @Column(nullable = false)
    private LocalDate datePrime;

    private String observation;
        @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

}