package com.king.peace.Dto.Response;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MonthlyStatDto {
  private int month;
  private double total;
}