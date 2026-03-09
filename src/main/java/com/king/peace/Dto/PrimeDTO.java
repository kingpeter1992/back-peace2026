package com.king.peace.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.king.peace.Entitys.Devise;
import com.king.peace.enums.StatutPrime;
import com.king.peace.enums.TypePrime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrimeDTO {
  private Long id;
    private Long gardienId;
    private String gardienNom;
    private Double montant;
    private Devise devise;
    private LocalDate datePrime;
    private TypePrime typePrime;
    private String motif;
    private StatutPrime statut;
    private Integer moisConcerne;
    private Integer anneeConcerne;
    private String observation;

}