package com.king.peace.Dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationForceDTO {
    private Long gardienId;
//    private Integer mois;
//    private Integer annee;
private LocalDate datePaieDebut;
private LocalDate datePaieFin;
    private LocalDate datePaie;
    private String observation;
}