package com.king.peace.Entitys;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_finance_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CustomerFinanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // SALAIRE, AVANCE, PRET

    private Double montant;
    private LocalDateTime datePaiement;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne
    @JoinColumn(name = "transaction_caisse_id")
    private TransactionCaisse transactionCaisse;

    @PrePersist
    void prePersist() {
        datePaiement = LocalDateTime.now();
    }
}