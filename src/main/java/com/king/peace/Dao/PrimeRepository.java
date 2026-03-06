package com.king.peace.Dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Prime;

import java.time.LocalDate;
import java.util.List;

public interface PrimeRepository extends JpaRepository<Prime, Long> {
    List<Prime> findByGardienIdAndDatePrimeBetween(Long gardienId, LocalDate debut, LocalDate fin);
}
