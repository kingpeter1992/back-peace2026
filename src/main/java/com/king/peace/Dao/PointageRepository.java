package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Pointage;
@Repository
public interface PointageRepository extends JpaRepository<Pointage,Long>{

    List<Pointage> findByGardienId(Long gardienId);
    List<Pointage> findByGardienIdAndDateBetween(
        Long gardienId,
        LocalDate dateFrom,
        LocalDate dateTo
);
}
