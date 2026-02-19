package com.king.peace.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Plaintes;

@Repository
public interface PlainteRepository extends JpaRepository<Plaintes, Long> {

    // Toutes les plaintes pour un gardien
    List<Plaintes> findByGardien_Id(Long gardienId);

    // Optionnel : pour récupérer les plus récentes
    List<Plaintes> findByGardien_IdOrderByDatePlainteDesc(Long gardienId);

    List<Plaintes> findByClientId(Long clientId);

    // Moyenne des notes d'un gardien
    @Query("SELECT AVG(p.note) FROM Plaintes p WHERE p.gardien.id = :gardienId")
    Double moyenneNote(@Param("gardienId") Long gardienId);


    @Query("""
        SELECT p FROM Plaintes p
        LEFT JOIN FETCH p.client
        LEFT JOIN FETCH p.gardien
        LEFT JOIN FETCH p.reponses
        WHERE p.id = :id
    """)
    Optional<Plaintes> findByIdWithRelations(@Param("id") Long id);

  @Query("""
    SELECT DISTINCT p FROM Plaintes p
    LEFT JOIN FETCH p.reponses
    """)
    List<Plaintes> findAllWithReponses();

}
