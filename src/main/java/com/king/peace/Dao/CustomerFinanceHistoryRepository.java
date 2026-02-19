package com.king.peace.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.CustomerFinanceHistory;
import com.king.peace.Entitys.Facture;

public interface CustomerFinanceHistoryRepository extends JpaRepository<CustomerFinanceHistory, Long> {

    /**
     * Récupère le dernier historique pour une facture donnée
     */
    Optional<CustomerFinanceHistory> findFirstByFactureOrderByDatePaiementDesc(Facture facture);

    /**
     * Récupère le dernier historique pour un client donné
     */
    Optional<CustomerFinanceHistory> findFirstByClientOrderByDatePaiementDesc(Client client);

    /**
     * Récupère tous les historiques pour un client donné
     */
    List<CustomerFinanceHistory> findByClientOrderByDatePaiementDesc(Client client);

    /**
     * Récupère tous les historiques pour une facture donnée
     */
    List<CustomerFinanceHistory> findByFactureOrderByDatePaiementDesc(Facture facture);

    /**
     * Vérifie s'il existe déjà un historique pour une facture spécifique
     */
    boolean existsByFacture(Facture facture);

    CustomerFinanceHistory findByFactureAndType(Facture f, String string);
}