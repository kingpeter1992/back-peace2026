package com.king.peace.Interfaces;

import java.util.List;

import com.king.peace.Dto.AvanceSalaireDTO;

public interface AvanceSalaireService {
       AvanceSalaireDTO save(AvanceSalaireDTO dto);
    List<AvanceSalaireDTO> findAll();
    AvanceSalaireDTO valider(Long id);
    void delete(Long id);
}
