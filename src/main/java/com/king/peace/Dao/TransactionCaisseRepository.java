package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.TransactionCaisse;
@Repository
public interface TransactionCaisseRepository extends JpaRepository<TransactionCaisse,Long> {
    
}
