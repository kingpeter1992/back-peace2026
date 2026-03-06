package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Retenue;

import java.time.LocalDate;
import java.util.List;

public interface RetenueRepository extends JpaRepository<Retenue, Long> {
    List<Retenue> findByGardienIdAndDateRetenueBetween(Long gardienId, LocalDate debut, LocalDate fin);
}
