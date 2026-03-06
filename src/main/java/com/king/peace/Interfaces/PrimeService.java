package com.king.peace.Interfaces;

import java.util.List;

import com.king.peace.Dto.PrimeDTO;

public interface PrimeService {
PrimeDTO save(PrimeDTO dto);
    List<PrimeDTO> findAll();
    void delete(Long id);
}
