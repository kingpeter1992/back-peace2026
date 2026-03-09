package com.king.peace.Dto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Devise;
import com.king.peace.enums.StatutPaie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieDTO {
    private Long id;
    private Long gardienId;
    private String gardienNom;
    private Integer mois;
    private Integer annee;
    private LocalDate datePaie;
    private double salaireBase;
    private Devise devise;
    private double totalPrimes;
    private double totalAvances;
    private double totalPrets;
    private double autresRetenues;
    private double netAPayer;
    private StatutPaie statut;
    private String observation;
    private List<PaieLigneDTO> lignes;
}