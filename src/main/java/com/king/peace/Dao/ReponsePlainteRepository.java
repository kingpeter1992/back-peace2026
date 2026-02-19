package com.king.peace.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.ReponsePlainte;

public interface ReponsePlainteRepository extends JpaRepository<ReponsePlainte,Long> {

    List<ReponsePlainte> findByPlainteId(Long plainteId);
        List<ReponsePlainte> findByPlainte_Id(Long plainteId);  // ✅ correspond à entity

    
}
