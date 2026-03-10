package com.king.peace.Dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationMasseDTO {
//    private Integer mois;
//    private Integer annee;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer totalGardiens;
    private Integer totalSucces;
    private Integer totalEchecs;
    private List<PaieGenerationItemDTO> details;

}