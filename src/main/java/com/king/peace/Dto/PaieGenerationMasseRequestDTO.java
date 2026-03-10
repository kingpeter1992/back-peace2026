package com.king.peace.Dto;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieGenerationMasseRequestDTO {
 private List<Long> gardienIds;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate datePaie;
    private String observation;
}