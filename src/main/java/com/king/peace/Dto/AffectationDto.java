package com.king.peace.Dto;

import lombok.*;
import java.time.LocalDate;

import com.king.peace.Entitys.StatutAffectation;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffectationDto {
    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long gardienId;
    private Long contratId;
    private StatutAffectation statut;
    private LocalDate dateAffectation;
    private String  description;
    private boolean active;
    private String refContrats;
    private String site;
}
