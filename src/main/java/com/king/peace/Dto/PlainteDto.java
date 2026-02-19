package com.king.peace.Dto;

import java.time.LocalDate;
import java.util.List;

import com.king.peace.Entitys.NiveauPlainte;
import com.king.peace.Entitys.ReponsePlainte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor  @AllArgsConstructor @Builder
public class PlainteDto {
  
private Long id;
    private String description;
    private LocalDate datePlainte;
    private String statut;
    private Integer note;
    private String gardienNom;
    private String clientNom;
    private NiveauPlainte niveau;
    private LocalDate dateLimiteReponse;
    private String reponseGardien;
    private boolean repondu;
    private Long clientId;
    private Long gardienId;
    private boolean active;
    private List<ReponseDto> listeReponses;


    
}
