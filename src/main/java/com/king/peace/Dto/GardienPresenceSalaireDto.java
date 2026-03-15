package com.king.peace.Dto;

import java.time.LocalDate;
import com.king.peace.Entitys.Devise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GardienPresenceSalaireDto {
    private Long gardienId;
    private String nomComplet;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer nbrJoursAPrester;
    private double salaireBase;
    private double montantParJour;
    private long nbrJoursPresents;
    private double montantTotalGagne;
    private Devise devise;
}