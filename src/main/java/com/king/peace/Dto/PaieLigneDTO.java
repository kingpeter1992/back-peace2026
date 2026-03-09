package com.king.peace.Dto;

import com.king.peace.enums.SensLignePaie;
import com.king.peace.enums.TypeLignePaie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieLigneDTO {
    private Long id;
    private TypeLignePaie typeLigne;
    private Long referenceId;
    private String libelle;
    private Double montant;
    private SensLignePaie sens;
}