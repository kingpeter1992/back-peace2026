package com.king.peace.Interfaces;

import java.util.List;

import com.king.peace.Dto.PretDTO;

public interface PretService {
     PretDTO save(PretDTO dto);
    List<PretDTO> findAll();
    void delete(Long id);
}
