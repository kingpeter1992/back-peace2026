package com.king.peace.Dao.dashDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MonthTotalDTO {
  private int year;
  private int month;      // 1..12
  private double total;
}