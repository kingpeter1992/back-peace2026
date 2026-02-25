package com.king.peace.Dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Plaintes;

@Repository
public interface PlainteRepository extends JpaRepository<Plaintes, Long> {

    // Toutes les plaintes pour un gardien
    List<Plaintes> findByGardien_Id(Long gardienId);

    // Optionnel : pour récupérer les plus récentes
    List<Plaintes> findByGardien_IdOrderByCreatedAtDesc(Long gardienId);

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

      List<Plaintes> findByClient_Id(Long clientId);

        // ✅ Plainte du client entre 2 dates
  List<Plaintes> findByClient_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
      Long clientId,
      LocalDate from,
      LocalDate to
  );

  // ✅ Plainte du gardien entre 2 dates
  List<Plaintes> findByGardien_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
      Long gardienId,
      LocalDate from,
      LocalDate to
  );


}
