package com.king.peace.Entitys;

import java.time.LocalDate;

import com.king.peace.enums.StatutPret;

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
import lombok.*;

@Entity
@Table(name = "prets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardien_id", nullable = false)
    private Gardien gardien;

    private double montantTotal;

    private double montantRestant;

    private Integer nombreMois;

    private double mensualite;

    private LocalDate dateDebut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPret statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;


    private String motif;
}