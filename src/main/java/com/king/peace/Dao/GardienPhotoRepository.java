package com.king.peace.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.GardienPhoto;

@Repository
public interface GardienPhotoRepository extends JpaRepository<GardienPhoto, Long> {
    Optional<GardienPhoto> findByGardienId(Long gardienId);

    GardienPhoto findFirstByGardienId(Long id);

       // Toutes les photos d’un gardien
    List<GardienPhoto> findAllByGardienId(Long gardienId);

    // Première photo d’un gardien (utile pour l'affichage)
    GardienPhoto findFirstByGardienIdOrderByIdAsc(Long gardienId);

    // Optionnel si tu veux envelopper dans Optional
    Optional<GardienPhoto> findTopByGardienIdOrderByIdAsc(Long gardienId);
}