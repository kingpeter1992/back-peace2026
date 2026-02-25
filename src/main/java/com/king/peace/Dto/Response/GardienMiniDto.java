package com.king.peace.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class GardienMiniDto {
  private Long id;
  private String nom;
  private String prenom;
  private String fonction;
}