package com.king.peace.Interfaces;

import java.util.List;

import com.king.peace.Dto.RetenueDTO;

public interface RetenueService {
RetenueDTO save(RetenueDTO dto);
    List<RetenueDTO> findAll();
    void delete(Long id);
}
