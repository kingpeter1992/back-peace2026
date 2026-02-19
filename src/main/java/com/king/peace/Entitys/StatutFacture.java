package com.king.peace.Entitys;

public enum StatutFacture {
    EMIS,       // Facture normale émise
    REFACTURE,  // Facture modifiée suite à réclamation
    ANNULE,      // Facture annulée totalement
    NEW,
    PAID
}