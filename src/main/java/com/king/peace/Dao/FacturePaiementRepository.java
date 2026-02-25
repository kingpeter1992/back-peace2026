package com.king.peace.Dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.FacturePaiement;
import com.king.peace.Entitys.StatutFacture;

public interface FacturePaiementRepository extends JpaRepository<FacturePaiement, Long>{
    
@Query("SELECT COALESCE(SUM(fp.montantAffecte),0) FROM FacturePaiement fp WHERE fp.transaction.id = :txId")
double sumAffecteByTransactionId(Long txId);
  List<FacturePaiement> findByFacture_Id(Long factureId);
  List<FacturePaiement> findByTransaction_Id(Long transactionId);
  @Query("""
    SELECT COALESCE(SUM(fp.montantAffecte),0)
    FROM FacturePaiement fp
    WHERE fp.facture.client.id = :clientId AND fp.facture.devise = :devise
  """)
  double sumAffecteByClientAndDevise(Long clientId, Devise devise);

}
