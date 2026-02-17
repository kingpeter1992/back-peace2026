package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Caisse;
@Repository
public interface CaisseRepository extends JpaRepository<Caisse,Long>{

    
}
