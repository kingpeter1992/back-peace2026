package com.king.peace.Dto.Response;

import java.util.List;

import com.king.peace.Entitys.Devise;

import lombok.Data;

@Data
public class LettrerManyRequest {
  private Long clientId;
  private Devise devise;
  private List<Long> factureIds; // dans l’ordre choisi
}