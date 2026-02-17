package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;

@Repository
public interface ContratRepository extends  JpaRepository<Contrats,Long>{

    List<Contrats> findByClient(Client client);

// Récupère tous les contrats actifs dont la dateDebut <= start et dateFin >= end
    List<Contrats> findByActiveTrueAndDateDebutBeforeAndDateFinAfter(LocalDate start, LocalDate end);
    
}
