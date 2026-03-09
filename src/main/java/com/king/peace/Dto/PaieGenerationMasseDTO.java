package com.king.peace.Dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationMasseDTO {
    private Integer mois;
    private Integer annee;
    private Integer totalGardiens;
    private Integer totalSucces;
    private Integer totalEchecs;
    private List<PaieGenerationItemDTO> details;
}