package com.king.peace.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieSuppressionItemDTO {
    private Long paieId;
    private boolean succes;
    private String message;
}