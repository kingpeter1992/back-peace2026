package com.king.peace.Dto;

import com.king.peace.Entitys.ModePaiement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RemboursementClientDTO {
    private Long clientId;
    private Double montant;
    private String motif;
    private ModePaiement modePaiement;
}

