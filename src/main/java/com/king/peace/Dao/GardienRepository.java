package com.king.peace.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
