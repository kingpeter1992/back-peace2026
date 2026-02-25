package com.king.peace.Dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.king.peace.Dto.Response.ContratDetailsMiniDto;
import com.king.peace.Dto.Response.FactureMiniDto;
import com.king.peace.Dto.Response.TransactionMiniDto;

@Getter @Setter
public class ClientDetailsDto {
  private ClientDto client;
  private List<FactureMiniDto> factures;
  private ClientStatsDto stats;
  private List<ContratDetailsMiniDto> contrats;  // avec gardiens attachés
  private List<TransactionMiniDto> transactions;
    private List<PlainteDto> plainte;

}