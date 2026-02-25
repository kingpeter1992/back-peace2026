package com.king.peace.Dto.Response;

import java.time.LocalDateTime;

import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ModePaiement;
import com.king.peace.Entitys.TypeTransaction;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TransactionMiniDto {
  private Long id;
  @Enumerated(EnumType.STRING)
  private TypeTransaction type; // ENCAISSEMENT, DECAISSEMENT

  @Enumerated(EnumType.STRING)
  private Devise devise; // USD, CDF

  private double montant;

  @Enumerated(EnumType.STRING)
  private CategorieOperation category;

  @Enumerated(EnumType.STRING)
  private ModePaiement modePaiement;

  private String description;
  private String reference;

  private double soldeAvant;

  private double soldeApres;

  private String sens; // + / -
  private String userId;

  private LocalDateTime dateTransaction;
}
