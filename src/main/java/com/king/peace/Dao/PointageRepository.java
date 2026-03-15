package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.StatutPointage;
@Repository
public interface PointageRepository extends JpaRepository<Pointage,Long>{

    List<Pointage> findByGardienId(Long gardienId);
    List<Pointage> findByGardienIdAndDateBetween(
        Long gardienId,
        LocalDate dateFrom,
        LocalDate dateTo
);
    List<Pointage> findByGardienIdAndMoisAndAnnee(Long gardienId, Integer mois, Integer annee);
        List<Pointage> findByGardien_IdAndMoisAndAnnee(Long gardienId, Integer mois, Integer annee);

@Query("""
    select p
    from Pointage p
    where p.gardien.id = :gardienId
      and p.statut in :statuts
      and p.date <= :dateFin
      and p.datesortie >= :dateDebut
    order by p.date asc
""")
List<Pointage> findByGardienIdAndPeriodeAndStatuts(
        @Param("gardienId") Long gardienId,
        @Param("statuts") List<StatutPointage> statuts,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
);

 @Query("""
        select p.gardien.id, count(p)
        from Pointage p
        where p.statut = :statut
          and p.date between :dateDebut and :dateFin
        group by p.gardien.id
    """)
    List<Object[]> countPresenceByGardienBetweenDates(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );
  @Query("""
        select p.gardien.id, count(p)
        from Pointage p
        where p.statut = :statut
          and p.date between :dateDebut and :dateFin
        group by p.gardien.id
    """)
    List<Object[]> countPresenceByGardienBetweenDates(
            @Param("statut") StatutPointage statut,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );}
