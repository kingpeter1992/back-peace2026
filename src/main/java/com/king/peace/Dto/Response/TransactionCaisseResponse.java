package com.king.peace.Dto.Response;

import java.time.LocalDateTime;

import org.hibernate.grammars.hql.HqlParser.LocalDateTimeContext;

import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.TypeTransaction;



public record TransactionCaisseResponse(
        Long id,
        String reference,
        Double montant,
        Devise devise,
        TypeTransaction type,
        CategorieOperation category,
        LocalDateTime dateTransaction,
        Double soldeAvant,
        Double soldeApres,
        String sens,
        Long clientId,
        Long gardienId,
        Long sessionId
) {}