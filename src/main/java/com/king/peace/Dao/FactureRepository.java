package com.king.peace.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Facture;

@Repository
public interface FactureRepository extends JpaRepository<Facture,Long> {

    List<Facture> findByClientId(Long clientId);

    List<Facture> findByClient(Client client);


    
}
