package com.king.peace.Entitys;

import java.time.LocalDateTime;

import org.aspectj.weaver.loadtime.Agent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TransactionCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- clé primaire obligatoire
    @Enumerated(EnumType.STRING)
    private TypeTransaction type; // ENCAISSEMENT, DECAISSEMENT, AVANCE, PRET
    private double montant;
    @Enumerated(EnumType.STRING)
    private Devise devise;   // 🔥 USD / CDF
    private String description;
    @Enumerated(EnumType.STRING)
    private CategorieOperation category;
    // SALAIRE, AVANCE, PRET, FACTURE, REMBOURSEMENT, AUTRE
    private String sens;
    private String reference;
    private double soldeAvant;
    private double soldeApres;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement; // CASH, MOBILE_MONEY, BANQUE

    private LocalDateTime dateTransaction;

    @ManyToOne
    @JoinColumn(name = "caisse_id")
    private Caisse caisse;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client; // seulement si c'est un paiement client

    @ManyToOne
    @JoinColumn(name = "gardien_id", nullable = true)
    private Gardien gardien; // nullable

}
