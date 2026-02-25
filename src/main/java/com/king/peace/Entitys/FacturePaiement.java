package com.king.peace.Entitys;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="facture_paiement")
@Setter 
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FacturePaiement {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false)
  @JoinColumn(name="facture_id")
  private Facture facture;

  @ManyToOne(optional=false)
  @JoinColumn(name="transaction_id")
  private TransactionCaisse transaction;

  private double montantAffecte;

  private LocalDateTime createdAt = LocalDateTime.now();

  private String createdBy; // optionnel
}