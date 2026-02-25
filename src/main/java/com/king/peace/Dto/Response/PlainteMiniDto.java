package com.king.peace.Dto.Response;

import java.time.LocalDate;

import com.king.peace.Entitys.NiveauPlainte;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PlainteMiniDto {
     private Long id;
    private LocalDate datePlainte;
    private String description;
    private int note; // 0 à 10
    @Enumerated(EnumType.STRING)
    private NiveauPlainte niveau;
    private boolean repondu = false;
    private LocalDate dateLimiteReponse;
    private String response;
    private LocalDate dateRespnse;
    private String statut;
    private LocalDate createdAt;
    private String reponseGardien;
    private boolean active = true;
}