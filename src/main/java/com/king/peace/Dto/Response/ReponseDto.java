package com.king.peace.Dto.Response;

import java.time.LocalDateTime;

public class ReponseDto {
  private Long id;
  private String message;
  private String auteur;      // ex: "GARDIEN" ou username
  private LocalDateTime createdAt;
}