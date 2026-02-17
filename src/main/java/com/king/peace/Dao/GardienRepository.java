package com.king.peace.Dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutGardien;

@Repository
public interface GardienRepository  extends  JpaRepository<Gardien, Long> {
        long countByStatut(StatutGardien statut);
        List<Gardien> findByActifTrue();
    
}
