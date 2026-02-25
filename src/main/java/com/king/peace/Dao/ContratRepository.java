package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.StatutGardien;

@Repository
public interface ContratRepository extends  JpaRepository<Contrats,Long>{

    List<Contrats> findByClient(Client client);
    List<Contrats> findByStatut(StatutGardien statut);

// Récupère tous les contrats actifs dont la dateDebut <= start et dateFin >= end
    List<Contrats> findByActiveTrueAndDateDebutBeforeAndDateFinAfter(LocalDate start, LocalDate end);
    List<Contrats> findByActiveTrue();
    boolean existsByRefContrats(String refContrats);
    List<Contrats> findByClient_Id(Long clientId);

   long countByClientIdAndDateDebutBetween(
        Long clientId,
        LocalDate dateFrom,
        LocalDate dateTo
);

long countByClientIdAndActiveAndDateDebutBetween(
    Long clientId,
    boolean active,
    LocalDate dateFrom,
    LocalDate dateTo
);
List<Contrats> findByClientIdAndDateDebutBetween(Long clientId, LocalDate dateFrom, LocalDate dateTo);
List<Contrats> findByClientIdAndCreatedAtBetween(
    Long clientId,
    LocalDate dateFrom,
    LocalDate dateTo
);
long countByClientIdAndCreatedAtBetween(Long clientId, LocalDate dateFrom, LocalDate dateTo);
long countByClientIdAndActiveAndCreatedAtBetween(Long clientId, boolean b, LocalDate dateFrom, LocalDate dateTo);
}
