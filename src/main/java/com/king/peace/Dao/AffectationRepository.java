package com.king.peace.Dao;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutAffectation;


@Repository
public interface AffectationRepository extends JpaRepository<Affectation,Long>{

    Optional<Affectation> findByGardienIdAndStatut(Long gardienId, StatutAffectation active);

    boolean existsByGardienId(Long gardienId);

    boolean existsByGardienAndActiveTrue(Gardien gardien);
  List<Affectation> findByContratIdAndActiveTrue(Long contratId);

    // ✅ Vérifie qu’il existe une affectation ACTIVE du gardien sur un contrat de ce client
    boolean existsByGardienIdAndContrat_Client_IdAndStatut(
            Long gardienId,
            Long clientId,
            StatutAffectation statut
    );

    @Query("""
       SELECT COUNT(a)
       FROM Affectation a
       WHERE a.contrat.id = :contratId
       AND a.active = true
       """)
Long countActiveByContratId(Long contratId);
}
