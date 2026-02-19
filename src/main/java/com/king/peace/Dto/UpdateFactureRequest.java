package com.king.peace.Dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateFactureRequest {
     private Integer nombreGardiens;
    private Double montantParGardien;
    private Double discount; // ex: montant fixe (ou % si tu veux)
    private String notes;
    private Boolean confirm; // si true => passer à EMIS
}
