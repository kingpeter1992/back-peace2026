package com.king.peace.Dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.king.peace.Entitys.StatutSessionCaisse;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CaisseSessionDto {

    private Long id;
    private LocalDate dateJour;
    private StatutSessionCaisse statut;

    private LocalDateTime dateOuverture;
    private LocalDateTime dateCloture;

    private double soldeInitialUSD;
    private double soldeInitialCDF;

    private double soldeActuelUSD;
    private double soldeActuelCDF;

    private String openedBy;
    private String closedBy;

    private String noteOuverture;
    private String noteCloture;
}