package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.PaiementSalaire;

import java.util.List;
import java.util.Optional;

public interface PaiementSalaireRepository extends JpaRepository<PaiementSalaire, Long> {
    Optional<PaiementSalaire> findByGardienIdAndMoisAndAnnee(Long gardienId, Integer mois, Integer annee);
    List<PaiementSalaire> findByMoisAndAnnee(Integer mois, Integer annee);
}