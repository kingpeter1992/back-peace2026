package com.king.peace.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.king.peace.enums.StatutPret;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PretDTO {
    private Long id;
    private Long employeId;
    private String employeNom;
    private double montantTotal;
    private double montantRestant;
    private Integer nombreMois;
    private double mensualite;
    private LocalDate dateDebut;
    private StatutPret statut;
    private String motif;
}
