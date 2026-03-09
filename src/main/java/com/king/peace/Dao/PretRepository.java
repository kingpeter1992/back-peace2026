package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Pret;
import com.king.peace.enums.StatutPret;

public interface PretRepository extends JpaRepository<Pret, Long> {
    List<Pret> findByGardienIdAndStatut(Long gardienId, StatutPret statut);


     @Query("""
        select coalesce(sum(p.mensualite),0)
        from Pret p
        where p.devise = :devise
        and p.statut = :statut
        and p.dateDebut <= :dateFin
    """)
    double sumMensualiteByDeviseAndStatutUntilDate(
            Devise devise,
            StatutPret statut,
            LocalDate dateFin
    );

 @Query("SELECT p FROM Pret p LEFT JOIN FETCH p.gardien")
    List<Pret> findAllWithGardien();


@Query("""
        SELECT p
        FROM Pret p
        WHERE p.gardien.id = :gardienId
          AND p.statut = :statut
          AND p.devise = :devise
    """)
    List<Pret> findPretsEnCoursPourPaie(
            @Param("gardienId") Long gardienId,
            @Param("statut") StatutPret statut,
            @Param("devise") Devise devise
    );


    @Query("""
    SELECT COALESCE(SUM(p.mensualite),0)
    FROM Pret p
    WHERE p.gardien.id = :gardienId
    AND p.statut = :statut
    AND p.devise = :devise
""")
double sumMensualitePretsPourPaie(
        @Param("gardienId") Long gardienId,
        @Param("statut") StatutPret statut,
        @Param("devise") Devise devise
);
}