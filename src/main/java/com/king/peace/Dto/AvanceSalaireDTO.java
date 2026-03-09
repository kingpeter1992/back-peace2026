package com.king.peace.Dto;

import lombok.*;
import java.time.LocalDate;
import com.king.peace.Entitys.Devise;
import com.king.peace.enums.StatutAvance;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvanceSalaireDTO {
    private Long id;
    private Long gardienId;
    private String gardienNom;
    private double montant;
    private LocalDate dateAvance;
    private StatutAvance statut;
    private String observation;
    private Devise devise;
    private Integer moisConcerne;
    private Integer anneeConcerne;
    private String motif;

}