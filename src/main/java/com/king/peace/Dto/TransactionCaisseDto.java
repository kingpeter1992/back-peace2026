package com.king.peace.Dto;

import java.time.LocalDateTime;

import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ModePaiement;
import com.king.peace.Entitys.TypeTransaction;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TransactionCaisseDto {
    private Long id;
    private double montant;
    private Devise devise;
    private String description;
    private LocalDateTime dateTransaction;
    private Long gardienId;   // optionnel
    private Long clientId;  // optionnel
    private  TypeTransaction type;
    private CategorieOperation category;
        private String sens;
        private String NomGardien;
        private String NomClient;
        private double soldeAvant;
        private double soldeApres;
    private String reference;
    private ModePaiement modePaiement; // CASH, MOBILE_MONEY, BANQUE


}
