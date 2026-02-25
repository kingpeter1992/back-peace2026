package com.king.peace.Dto.Response;

import java.util.List;

import com.king.peace.Entitys.Devise;

import lombok.Data;

@Data
public class LettrageContextDto {
  private Long clientId;
  private Devise devise;
  private double creditDisponible;
  private List<FactureMiniDto> factures;
}