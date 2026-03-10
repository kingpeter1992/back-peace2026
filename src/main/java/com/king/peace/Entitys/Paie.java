package com.king.peace.Entitys;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.king.peace.enums.StatutPaie;

@Entity
@Table(name = "paies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gardien_id", nullable = false)
  private Gardien gardien;

  // @Column(nullable = false)
  // private Integer mois;

  @Column(name = "numero_bulletin", unique = true)
  private String numeroBulletin;

  // @Column(nullable = false)
  // private Integer annee;

    @Column(name = "date_paie", nullable = false)
  private LocalDate datePaie;

  @Column(name = "date_paie_debut", nullable = false)
  private LocalDate datePaieDebut;

  @Column(name = "date_paie_fin", nullable = false)
  private LocalDate datePaieFin;



  @Column(name = "salaire_base", nullable = false)
  private double salaireBase;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Devise devise;

  @Column(name = "total_primes", nullable = false)
  private double totalPrimes;

  @Column(name = "total_avances", nullable = false)
  private double totalAvances;

  @Column(name = "total_prets", nullable = false)
  private double totalPrets;

  @Column(name = "autres_retenues", nullable = false)
  private double autresRetenues;

  @Column(name = "net_a_payer", nullable = false)
  private double netAPayer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutPaie statut;

  @Column(length = 1000)
  private String observation;

  // ✅ RELATION CORRECTE POUR LES LIGNRES
  @OneToMany(mappedBy = "paies", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PaieLigne> paieLignes = new ArrayList<>();
}
