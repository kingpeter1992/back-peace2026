package com.king.peace.Dto;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ModePaiement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncaissementAgentDTO {
    private Long agentId;
    private Double montant;
    private String motif;
    private ModePaiement modePaiement;
    private Devise devise;
}
