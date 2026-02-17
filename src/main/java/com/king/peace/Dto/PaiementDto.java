package com.king.peace.Dto;

import java.time.LocalDate;
import java.util.List;


public class PaiementDto {
    private Long id;
    private java.time.LocalDate date;
    private double montant;
    private String mode;
    List<TransactionCaisseDto> transactiondCaisseDto;
    public Long getId() {
        return id;
    }
    public java.time.LocalDate getDate() {
        return date;
    }
    public double getMontant() {
        return montant;
    }
    public String getMode() {
        return mode;
    }
    public List<TransactionCaisseDto> getTransactiondCaisseDto() {
        return transactiondCaisseDto;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setDate(java.time.LocalDate date) {
        this.date = date;
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public void setTransactiondCaisseDto(List<TransactionCaisseDto> transactiondCaisseDto) {
        this.transactiondCaisseDto = transactiondCaisseDto;
    }
    public PaiementDto(Long id, LocalDate date, double montant, String mode,
            List<TransactionCaisseDto> transactiondCaisseDto) {
        this.id = id;
        this.date = date;
        this.montant = montant;
        this.mode = mode;
        this.transactiondCaisseDto = transactiondCaisseDto;
    }
    public PaiementDto() {
    }

    
    
}
