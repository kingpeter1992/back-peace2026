package com.king.peace.Dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenererPaieRequest {
    private Long gardienId;
    private Integer mois;
    private Integer annee;
}