package com.king.peace.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GardienDetailsDto {
     private GardienDto gardien;
    private List<TransactionCaisseDto> paiements;
    private List<PresenceDto> presences;
    private List<PlainteDto> plainte;
    private List<String> photos; // Base64 de toutes les photos
    private GardienStatsDto stats;

}
