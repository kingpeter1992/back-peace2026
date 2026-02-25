package com.king.peace.Dto.Response;

import com.king.peace.Entitys.StatutFacture;

import lombok.Data;

@Data
public class LettrerLine {
  private Long factureId;
  private String refFacture;
  private StatutFacture statutAvant;
  private StatutFacture statutApres;
  private double resteAvant;
  private double affecte;
  private double resteApres;
}