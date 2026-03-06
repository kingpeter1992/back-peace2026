package com.king.peace.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.king.peace.enums.TypeRetenue;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RetenueDTO {
    private Long id;
    private Long employeId;
    private String employeNom;
    private TypeRetenue typeRetenue;
    private String libelle;
    private double montant;
    private LocalDate dateRetenue;
    private String motif;
}