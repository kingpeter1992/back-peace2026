package com.king.peace.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.king.peace.Entitys.Devise;
import com.king.peace.enums.TypePrime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrimeDTO {
    private Long id;
    private Long employeId;
    private String employeNom;
    private TypePrime typePrime;
    private String libelle;
    private double montant;
    private LocalDate datePrime;
    private String observation;
    private Devise devise;

}