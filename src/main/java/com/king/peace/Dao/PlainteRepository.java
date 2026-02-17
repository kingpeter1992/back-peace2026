package com.king.peace.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Plaintes;

public interface PlainteRepository  extends JpaRepository<Plaintes,Long>{

     // Toutes les plaintes pour un gardien
    List<Plaintes> findByGardien_Id(Long gardienId);
     // Optionnel : pour récupérer les plus récentes
    List<Plaintes> findByGardien_IdOrderByDatePlaiteDesc(Long gardienId);
}
