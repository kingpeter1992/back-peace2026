package com.king.peace.Dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.king.peace.Entitys.StatutPointage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointageMasseDTO {
      private Long gardienId;
    private LocalDate date;
    private LocalDate datesortie;
    private LocalTime heureEntree;
    private LocalTime heureSortie;
    private StatutPointage statuses;
}
