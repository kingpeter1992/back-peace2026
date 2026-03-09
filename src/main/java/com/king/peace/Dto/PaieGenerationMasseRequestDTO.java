package com.king.peace.Dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationMasseRequestDTO {
    private Integer mois;
    private Integer annee;
    private LocalDate datePaie;
    private String observation;
}