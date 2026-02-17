package com.king.peace.Utiltys;

import java.time.temporal.ChronoUnit;

import com.king.peace.Dto.PointageDto;
import com.king.peace.Dto.PresenceDto;
import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.StatutPointage;

// PointageMapper.java

public class PresenceMapper {
   public static PresenceDto toDto(Pointage p) {
        // Nombre de jours de présence
        int jours = (p.getDatesortie() != null && p.getDate() != null)
                ? (int) ChronoUnit.DAYS.between(p.getDate(), p.getDatesortie()) + 1
                : 1;

        int presence = p.getStatut() == StatutPointage.PRESENT ? 1 : 0;
        int absence = p.getStatut() == StatutPointage.ABSENT ? 1 : 0;

        PointageDto pdto = PointageDto.builder()
                .id(p.getId())
                .gardienId(p.getGardien().getId())
                .date(p.getDate())
                .datesortie(p.getDatesortie())
                .heureEntree(p.getHeureEntree())
                .heureSortie(p.getHeureSortie())
                .statut(p.getStatut().name())
                .build();

      return PresenceDto.builder()
        .totalPresence(jours)  // camelCase matches the field
        .presence(presence)
        .absence(absence)
        .pointage(pdto)
        .build();
    }
}