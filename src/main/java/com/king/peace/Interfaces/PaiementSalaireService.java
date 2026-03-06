package com.king.peace.Interfaces;

import java.util.List;

import com.king.peace.Dto.GenererPaieRequest;
import com.king.peace.Dto.PaiementSalaireDTO;

public interface PaiementSalaireService {
    PaiementSalaireDTO genererPaie(GenererPaieRequest request);
    PaiementSalaireDTO validerPaie(Long id);
    List<PaiementSalaireDTO> findAll();
}
