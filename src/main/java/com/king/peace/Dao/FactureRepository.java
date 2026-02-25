package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutFacture;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

  List<Facture> findByClientId(Long clientId);

  List<Facture> findByClient(Client client);

  Optional<Facture> findLastByContrats(Contrats contrat);

  boolean existsByContratsIdAndDateEmission(Long contratId, LocalDate dateEmission);

  Optional<Facture> findTopByContratsIdOrderByDateCycleDesc(Long contratId);

  boolean existsByContratsAndDateCycle(Contrats contrat, LocalDate dateCycle);

  Optional<Facture> findTopByContratsOrderByDateCycleDesc(Contrats contrat);

  @Query(value = """
        SELECT EXTRACT(MONTH FROM f.date_emission) AS month,
               COALESCE(SUM(f.montant_total),0) AS total
        FROM facture f
        WHERE f.date_emission BETWEEN :from AND :to
          AND (:clientId IS NULL OR f.client_id = :clientId)
          AND f.statut IN ('EMIS','PAID') -- adapte si tu veux inclure NEW
        GROUP BY EXTRACT(MONTH FROM f.date_emission)
        ORDER BY month
      """, nativeQuery = true)
  List<Object[]> monthlyStats(@Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("clientId") Long clientId);

  boolean existsByFactureOrigineId(Long factureOrigineId);

List<Facture> findByClientIdAndDateEmissionBetween(
        Long clientId,
        LocalDate dateFrom,
        LocalDate dateTo
);

List<Facture> findByClient_IdAndDeviseAndStatutInOrderByDateEmissionAsc(
    Long clientId,
    Devise devise,
    List<StatutFacture> statuts);

    
List<Facture> findByClient_IdAndDeviseAndStatutAndMotifAvoirIsNullOrderByDateEmissionAsc(
    Long clientId,
    Devise devise,
    StatutFacture statut
);
}
