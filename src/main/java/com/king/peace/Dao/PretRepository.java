package com.king.peace.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Pret;
import com.king.peace.enums.StatutPret;

public interface PretRepository extends JpaRepository<Pret, Long> {
    List<Pret> findByGardienIdAndStatut(Long gardienId, StatutPret statut);
}