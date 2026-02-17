package com.king.peace.Dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.TauxJournalier;
@Repository
public interface TauxJournalierRepository extends JpaRepository<TauxJournalier, Long>{
    Optional<TauxJournalier> findTopByOrderByDateDesc();

    
}
