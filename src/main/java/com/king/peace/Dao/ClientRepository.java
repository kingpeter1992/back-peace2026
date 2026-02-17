package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;

@Repository
public interface ClientRepository extends  JpaRepository<Client,Long>{
    
}
