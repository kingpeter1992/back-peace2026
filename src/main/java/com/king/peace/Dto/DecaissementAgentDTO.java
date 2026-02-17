package com.king.peace.Dto;

import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ModePaiement;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class DecaissementAgentDTO {
    private Long agentId;
    private Double montant;
    private CategorieOperation categorie; // PRET, AVANCE, SALAIRE
    private ModePaiement modePaiement;
     private Devise devise; // 🔥
}

