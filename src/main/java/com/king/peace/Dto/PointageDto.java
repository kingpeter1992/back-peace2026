package com.king.peace.Dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class PointageDto {
    private Long id;
    private LocalDate date;      // date d'entrée
    private LocalDate datesortie; // date de sortie
    private LocalTime heureEntree;
    private LocalTime heureSortie;
    private String statut;
    private Long gardienId;
    
}
