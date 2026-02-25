package com.king.peace.Dto.Response;

import java.util.List;

import lombok.Data;

@Data
public class LettrerManyResult {
  private double creditAvant;
  private double creditApres;
  private List<LettrerLine> lignes;
}