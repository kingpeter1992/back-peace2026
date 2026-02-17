package com.king.peace.Dto;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ModePaiement;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class DepenseDTO {
    private Double montant;
    private String description;
    private ModePaiement modePaiement;
    private Devise devise; // 🔥
}   