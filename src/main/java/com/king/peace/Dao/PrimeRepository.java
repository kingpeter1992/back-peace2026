package com.king.peace.Dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Prime;
import com.king.peace.enums.StatutPrime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrimeRepository extends JpaRepository<Prime, Long> {
    List<Prime> findByGardienIdAndDatePrimeBetween(Long gardienId, LocalDate debut, LocalDate fin);

      @Query("""
        select coalesce(sum(p.montant),0)
        from Prime p
        where p.devise = :devise
        and p.datePrime between :dateDebut and :dateFin
    """)
    double sumMontantByDeviseAndPeriod(Devise devise, LocalDate dateDebut, LocalDate dateFin);

    @Query("""
        SELECT p
        FROM Prime p
        LEFT JOIN FETCH p.gardien
        ORDER BY p.id DESC
    """)
    List<Prime> findAllWithGardien();

    @Query("""
        SELECT p
        FROM Prime p
        LEFT JOIN FETCH p.gardien
        WHERE p.id = :id
    """)
    Optional<Prime> findByIdWithGardien(@Param("id") Long id);

    List<Prime> findByMoisConcerneAndAnneeConcerne(Integer moisConcerne, Integer anneeConcerne);

    @Query("""
        SELECT COALESCE(SUM(p.montant), 0)
        FROM Prime p
        WHERE p.gardien.id = :gardienId
          AND p.moisConcerne = :moisConcerne
          AND p.anneeConcerne = :anneeConcerne
          AND p.statut = :statut
    """)
    Double sumMontantByGardienAndMoisAndAnneeAndStatut(
            @Param("gardienId") Long gardienId,
            @Param("moisConcerne") Integer moisConcerne,
            @Param("anneeConcerne") Integer anneeConcerne,
            @Param("statut") StatutPrime statut
    );

@Query("""
    SELECT p
    FROM Prime p
    WHERE p.gardien.id = :gardienId
      AND p.moisConcerne = :mois
      AND p.anneeConcerne = :annee
      AND p.statut = :statut
      AND p.devise = :devise
""")
List<Prime> findPrimesPourPaie(Long gardienId, Integer mois, Integer annee, StatutPrime statut, Devise devise);
  

@Query("""
    SELECT COALESCE(SUM(p.montant),0)
    FROM Prime p
    WHERE p.gardien.id = :gardienId
    AND p.datePrime BETWEEN :dateDebut AND :dateFin
    AND p.statut = :statut
    AND p.devise = :devise
""")
double sumPrimesPourPaie(
        @Param("gardienId") Long gardienId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin,
        @Param("statut") StatutPrime statut,
        @Param("devise") Devise devise
);
}
