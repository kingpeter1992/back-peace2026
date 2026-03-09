package com.king.peace.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationItemDTO {
    private Long gardienId;
    private String gardienNom;
    private boolean succes;
    private String message;
    private PaieDTO paie;
}