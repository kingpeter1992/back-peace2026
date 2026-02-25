package com.king.peace.Dto;

import java.time.LocalDateTime;

public record TxReportDTO(
        Long id,
        LocalDateTime dateTransaction,
        String reference,
        String category,
        String type,
        String devise,
        String modePaiement,
        double montant,
        double soldeAvant,
        double soldeApres,
        String description,
        Long sessionId,
        Long clientId,
        Long gardienId
) {}