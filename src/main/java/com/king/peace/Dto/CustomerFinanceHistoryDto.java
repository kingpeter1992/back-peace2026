package com.king.peace.Dto;

import java.time.LocalDateTime;

import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.TransactionCaisse;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class CustomerFinanceHistoryDto {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;
    private LocalDateTime datePaiement;
    private Facture facture;
    private TransactionCaisse transactionCaisse;

    @PrePersist
    void prePersist() {
        datePaiement = LocalDateTime.now();
}
}