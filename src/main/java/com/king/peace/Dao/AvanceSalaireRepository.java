package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Devise;
import com.king.peace.enums.StatutAvance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvanceSalaireRepository extends JpaRepository<AvanceSalaire, Long> {
    List<AvanceSalaire> findByGardienIdAndStatut(Long gardienId, StatutAvance statut);

    @Query("""
                select coalesce(sum(a.montant),0)
                from AvanceSalaire a
                where a.devise = :devise
                and a.statut in :statuts
                and a.dateAvance between :dateDebut and :dateFin
            """)
    double sumMontantByDeviseAndStatutAndPeriod(
            Devise devise,
            java.util.List<StatutAvance> statuts,
            LocalDate dateDebut,
            LocalDate dateFin);

    @Query("SELECT a FROM AvanceSalaire a LEFT JOIN FETCH a.gardien ORDER BY a.id DESC")
    List<AvanceSalaire> findAllWithGardien();

    List<AvanceSalaire> findByMoisConcerneAndAnneeConcerne(Integer moisConcerne, Integer anneeConcerne);

    @Query("""
                SELECT COALESCE(SUM(a.montant), 0)
                FROM AvanceSalaire a
                WHERE a.gardien.id = :gardienId
                  AND a.moisConcerne = :moisConcerne
                  AND a.anneeConcerne = :anneeConcerne
                  AND a.statut = :statut
            """)
    Double sumMontantByGardienAndMoisAndAnneeAndStatut(
            @Param("gardienId") Long gardienId,
            @Param("moisConcerne") Integer moisConcerne,
            @Param("anneeConcerne") Integer anneeConcerne,
            @Param("statut") StatutAvance statut);

  @Query("""
    SELECT COALESCE(SUM(a.montant), 0)
    FROM AvanceSalaire a
    WHERE a.gardien.id = :gardienId
      AND a.moisConcerne = :moisConcerne
      AND a.anneeConcerne = :anneeConcerne
      AND a.statut = :statut
      AND (:idExclu IS NULL OR a.id <> :idExclu)
""")
Double sumMontantByGardienAndMoisAndAnneeAndStatutAndIdNot(
        @Param("gardienId") Long gardienId,
        @Param("moisConcerne") Integer moisConcerne,
        @Param("anneeConcerne") Integer anneeConcerne,
        @Param("statut") StatutAvance statut,
        @Param("idExclu") Long idExclu
);

 @Query("""
    SELECT COALESCE(SUM(a.montant), 0)
    FROM AvanceSalaire a
    WHERE a.gardien.id = :gardienId
      AND a.moisConcerne = :moisConcerne
      AND a.anneeConcerne = :anneeConcerne
      AND a.statut = :statut
      AND a.devise = :devise
""")
Double sumMontantByGardienAndMoisAndAnneeAndStatutAndDevise(
        @Param("gardienId") Long gardienId,
        @Param("moisConcerne") Integer moisConcerne,
        @Param("anneeConcerne") Integer anneeConcerne,
        @Param("statut") StatutAvance statut,
        @Param("devise") Devise devise
);


@Query("""
    SELECT a
    FROM AvanceSalaire a
    LEFT JOIN FETCH a.gardien
    WHERE a.id = :id
""")
Optional<AvanceSalaire> findByIdWithGardien(@Param("id") Long id);

@Query("""
    SELECT a
    FROM AvanceSalaire a
    WHERE a.gardien.id = :gardienId
      AND a.moisConcerne = :mois
      AND a.anneeConcerne = :annee
      AND a.statut = :statut
      AND a.devise = :devise
""")
List<AvanceSalaire> findAvancesPourPaie(Long gardienId, Integer mois, Integer annee, StatutAvance statut, Devise devise);


@Query("""
    SELECT COALESCE(SUM(a.montant),0)
    FROM AvanceSalaire a
    WHERE a.gardien.id = :gardienId
    AND a.dateAvance BETWEEN :dateDebut AND :dateFin
    AND a.statut = :statut
    AND a.devise = :devise
""")
double sumAvancesPourPaie(
        @Param("gardienId") Long gardienId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin,
        @Param("statut") StatutAvance statut,
        @Param("devise") Devise devise
);
}