package com.king.peace.Dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutPointage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class PresenceDto {
 private Long id;
    private LocalDate date;
    private LocalDate datesortie;
    private LocalTime heureEntree;
    private LocalTime heureSortie;
    Gardien gardien;
    private PointageDto pointage;
    private StatutPointage statut;

    
    // Add these fields for presence mapping
    private int totalPresence; // note camelCase
    private int presence;
    private int absence;
}