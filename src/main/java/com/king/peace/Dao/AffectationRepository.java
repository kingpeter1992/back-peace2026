package com.king.peace.Dao;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutAffectation;


@Repository
public interface AffectationRepository extends JpaRepository<Affectation,Long>{

    Optional<Affectation> findByGardienIdAndStatut(Long gardienId, StatutAffectation active);

    boolean existsByGardienId(Long gardienId);

    boolean existsByGardienAndActiveTrue(Gardien gardien);

   

    
}
