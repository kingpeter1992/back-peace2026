package com.king.peace.Entitys;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateEmission;
    private double montantTotal;
    private StatutFacture statut;
    private String description;
    private String refFacture;
    private int nombreGardiens;
    private double montantParGardien; // <-- attention au nom exact !
    private double nombreJours;
    private Devise devise;
    @ManyToOne
    private Client client;
    @ManyToOne
    @JoinColumn(name = "contrats_id")
    private Contrats contrats;
}
