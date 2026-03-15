package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutGardien;

@Repository
public interface GardienRepository extends JpaRepository<Gardien, Long> {
        long countByStatut(StatutGardien statut);

        List<Gardien> findByActifTrue();

@Query(value = "SELECT COALESCE(SUM(salaire_base),0) FROM gardien WHERE devise = 0", nativeQuery = true)
double countSalairesCDF();

@Query(value = "SELECT COALESCE(SUM(salaire_base),0) FROM gardien WHERE devise = 1", nativeQuery = true)
double countSalairesUSD();

long countByActifTrue();

@Query("""
    select distinct g
    from Gardien g
    join Pointage p on p.gardien.id = g.id
    where g.actif = true
      and p.statut = com.king.peace.Entitys.StatutPointage.PRESENT
      and p.date <= :dateFin
      and p.datesortie >= :dateDebut
""")
List<Gardien> findActifsAvecPresenceDansPeriode(
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
);
}
