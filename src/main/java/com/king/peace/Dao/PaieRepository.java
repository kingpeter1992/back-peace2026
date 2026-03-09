package com.king.peace.Dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.peace.Entitys.Paie;

public interface PaieRepository  extends JpaRepository<Paie, Long>{

    boolean existsByGardienIdAndMoisAndAnnee(Long gardienId, Integer mois, Integer annee);

    @Query("""
        SELECT DISTINCT p
        FROM Paie p
        LEFT JOIN FETCH p.gardien
        LEFT JOIN FETCH p.paieLignes   
        ORDER BY p.id DESC
    """)
    List<Paie> findAllWithDetails();

    @Query("""
        SELECT DISTINCT p
        FROM Paie p
        LEFT JOIN FETCH p.gardien
        LEFT JOIN FETCH p.paieLignes   
        WHERE p.id = :id
    """)
    Optional<Paie> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT p
        FROM Paie p
        LEFT JOIN FETCH p.gardien
        LEFT JOIN FETCH p.paieLignes 
        WHERE p.mois = :mois AND p.annee = :annee
        ORDER BY p.id DESC
    """)
    List<Paie> findByMoisAndAnneeWithDetails(@Param("mois") Integer mois, @Param("annee") Integer annee);

    @Query("""
        SELECT DISTINCT p
        FROM Paie p
        LEFT JOIN FETCH p.gardien
        LEFT JOIN FETCH p.paieLignes 
        WHERE p.gardien.id = :gardienId
        ORDER BY p.id DESC
    """)
    List<Paie> findByGardienIdWithDetails(@Param("gardienId") Long gardienId);


    @Query("""
    SELECT p
    FROM Paie p
    LEFT JOIN FETCH p.gardien
    WHERE p.datePaie BETWEEN :dateDebut AND :dateFin
""")
List<Paie> findAllByPeriode(@Param("dateDebut") LocalDate dateDebut,
                            @Param("dateFin") LocalDate dateFin);
    
}
