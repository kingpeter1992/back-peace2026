package com.king.peace.Dto;

import java.time.LocalDateTime;


import com.king.peace.Entitys.TransactionCaisse;


import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class AgentFinanceHistoryDto {

    private Long id;
    private String type; // SALAIRE, AVANCE, PRET
    private Double montant;
    private LocalDateTime date;
    private GardienDto gardien;
    private TransactionCaisse transactionCaisse;
    public AgentFinanceHistoryDto() {
    }
    public AgentFinanceHistoryDto(Long id, String type, Double montant, LocalDateTime date, GardienDto gardien,
            TransactionCaisse transactionCaisse) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.date = date;
        this.gardien = gardien;
        this.transactionCaisse = transactionCaisse;
    }

    
}
