package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.enums.StatutAvance;

import java.util.List;

public interface AvanceSalaireRepository extends JpaRepository<AvanceSalaire, Long> {
    List<AvanceSalaire> findByGardienIdAndStatut(Long GardienId, StatutAvance statut);
}