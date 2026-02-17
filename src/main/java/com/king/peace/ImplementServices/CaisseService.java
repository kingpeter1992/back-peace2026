package com.king.peace.ImplementServices;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.king.peace.Dao.CaisseRepository;
import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.RepositoryAgentFinanceHistory;
import com.king.peace.Dao.RepositoryCustomerFinanceHistory;
import com.king.peace.Dao.TransactionCaisseRepository;
import com.king.peace.Dto.CustomerFinanceHistoryDto;
import com.king.peace.Dto.DecaissementAgentDTO;
import com.king.peace.Dto.DepenseDTO;
import com.king.peace.Dto.EncaissementAgentDTO;
import com.king.peace.Dto.EncaissementClientDTO;
import com.king.peace.Dto.RemboursementClientDTO;
import com.king.peace.Dto.SoldeCaisseDTO;
import com.king.peace.Dto.TransactionCaisseDto;
import com.king.peace.Entitys.AgentFinanceHistory;
import com.king.peace.Entitys.Caisse;
import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.CustomerFinanceHistory;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.ModePaiement;
import com.king.peace.Entitys.TransactionCaisse;
import com.king.peace.Entitys.TypeTransaction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CaisseService {

     private final CaisseRepository caisseRepository;
    private final TransactionCaisseRepository transactionRepository;
    private final GardienRepository gardienRepository;
    private final RepositoryAgentFinanceHistory agentFinanceHistoryRepository;
    private final RepositoryCustomerFinanceHistory customerFinanceHistoryRepository;
    private final ClientRepository clientRepository;
    private final FactureRepository factureRepository;
    
public SoldeCaisseDTO getSoldeCaisseCdf() {

    Caisse caisse = caisseRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

    SoldeCaisseDTO dto = new SoldeCaisseDTO();

    dto.setSoldeActuel(caisse.getMontantActuelCDF());
    dto.setDateDerniereOperation(caisse.getDate());

    return dto;
}

public SoldeCaisseDTO getSoldeCaisseUsd() {

    Caisse caisse = caisseRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

    SoldeCaisseDTO dto = new SoldeCaisseDTO();

    dto.setSoldeActuel(caisse.getMontantActuelUSD());
    dto.setDateDerniereOperation(caisse.getDate());

    return dto;
}


public List<TransactionCaisseDto> historiqueCaisse() {

    return transactionRepository
            .findAll(Sort.by(Sort.Direction.DESC, "dateTransaction"))
            .stream()
            .map(op -> {
                TransactionCaisseDto dto = new TransactionCaisseDto();
                dto.setId(op.getId());
                dto.setDateTransaction(op.getDateTransaction());
                dto.setType(op.getType());
                dto.setCategory(op.getCategory());
                dto.setMontant(op.getMontant());
                dto.setSens(op.getSens());
                dto.setDescription(op.getDescription());
                dto.setReference(op.getReference());
                dto.setModePaiement(op.getModePaiement());
                dto.setDevise(op.getDevise());
                dto.setSoldeAvant(op.getSoldeAvant());
                dto.setSoldeApres(op.getSoldeApres());

                if (op.getGardien() != null) {
                    dto.setNomGardien(op.getGardien().getNom());
                }
                if (op.getClient() != null) {
                    dto.setNomClient(op.getClient().getNom());
                }

                return dto;
            })
            .toList();
}


public TransactionCaisseDto encaissementClient(
        EncaissementClientDTO dto, Long userId) {

    Facture facture = factureRepository.findById(dto.getFactureId())
            .orElseThrow(() -> new RuntimeException("Facture introuvable"));

    TransactionCaisse op = enregistrerOperation(
            dto.getMontant(),
            TypeTransaction.ENCAISSEMENT,
            CategorieOperation.FACTURE,
            dto.getModePaiement(),
            "Paiement facture client",
            "FACT-" + facture.getId(),
            null,
            facture.getClient().getId(),
            userId, null
    );

    return mapToResponse(op);
}


public TransactionCaisseDto rembourserClient(
        RemboursementClientDTO dto, Long userId) {

    TransactionCaisse op = enregistrerOperation(
            dto.getMontant(),
            TypeTransaction.DECAISSEMENT,
            CategorieOperation.REMBOURSEMENT,
            dto.getModePaiement(),
            dto.getMotif(),
            "REM-" + System.currentTimeMillis(),
            null,
            dto.getClientId(),
            userId, null
    );

    return mapToResponse(op);
}


public TransactionCaisseDto depense(
        DepenseDTO dto, Long userId) {

    TransactionCaisse op = enregistrerOperation(
            dto.getMontant(),
            TypeTransaction.DECAISSEMENT,
            CategorieOperation.AUTRE,
            dto.getModePaiement(),
            dto.getDescription(),
            "DEP-" + System.currentTimeMillis(),
            null,
            null,
            userId, null
    );

    return mapToResponse(op);
}


public TransactionCaisseDto decaissementAgent(
        DecaissementAgentDTO dto, Long userId) {

    TransactionCaisse op = enregistrerOperation(
            dto.getMontant(),
            TypeTransaction.DECAISSEMENT,
            dto.getCategorie(),
            dto.getModePaiement(),
            dto.getCategorie().name() + " agent",
            dto.getCategorie().name() + "-" + dto.getAgentId(),
            dto.getAgentId(),
            null,
            userId, null
    );

    return mapToResponse(op);
}



public TransactionCaisseDto encaissementAgent(
        EncaissementAgentDTO dto, Long userId) {

    TransactionCaisse op = enregistrerOperation(
            dto.getMontant(),
            TypeTransaction.ENCAISSEMENT,
            CategorieOperation.AUTRE,
            dto.getModePaiement(),
            dto.getMotif(),
            "RET-" + dto.getAgentId(),
            dto.getAgentId(),
            null,
            userId, null
    );

    return mapToResponse(op);
}




private TransactionCaisseDto mapToResponse(TransactionCaisse op) {

    TransactionCaisseDto dto = new TransactionCaisseDto();
    dto.setId(op.getId());
    dto.setDateTransaction(op.getDateTransaction());
    dto.setType(op.getType());
    dto.setCategory(op.getCategory());
    dto.setMontant(op.getMontant());
    dto.setSens(op.getSens());
    dto.setDescription(op.getDescription());
    dto.setReference(op.getReference());
    dto.setModePaiement(op.getModePaiement());

    if (op.getGardien() != null) {
        dto.setNomClient(op.getGardien().getNom());
    }
    if (op.getClient() != null) {
        dto.setNomClient(op.getClient().getNom());
    }

    return dto;
}


private TransactionCaisse enregistrerOperation(
        Double montant,
        TypeTransaction type,
        CategorieOperation categorie,
        ModePaiement modePaiement,
        String description,
        String reference,
        Long gardienId,
        Long clientId,
        Long userId,
        Devise devise
) {


 if (gardienId != null && clientId != null) {
        throw new RuntimeException("Transaction invalide : agent et client à la fois");
    }

    Caisse caisse = caisseRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

    Double soldeActuel = devise == Devise.USD
            ? caisse.getMontantActuelUSD()
            : caisse.getMontantActuelCDF();

    // 🔐 Sécurité
    if (type == TypeTransaction.DECAISSEMENT && soldeActuel < montant) {
        throw new RuntimeException("Solde insuffisant en " + devise);
    }

    Double soldeApres = type == TypeTransaction.ENCAISSEMENT
            ? soldeActuel + montant
            : soldeActuel - montant;

    // 🔄 Mise à jour caisse
    if (devise == Devise.USD) {
        caisse.setMontantActuelUSD(soldeApres);
    } else {
        caisse.setMontantActuelCDF(soldeApres);
    }

    caisse.setDate(LocalDateTime.now());
    caisseRepository.save(caisse);

    // 🧾 Transaction
    TransactionCaisse tx = new TransactionCaisse();
    tx.setDateTransaction(LocalDateTime.now());
    tx.setType(type);
    tx.setCategory(categorie);
    tx.setMontant(montant);
    tx.setDevise(devise);
    tx.setSens(type == TypeTransaction.ENCAISSEMENT ? "+" : "-");
    tx.setSoldeAvant(soldeActuel);
    tx.setSoldeApres(soldeApres);
    tx.setDescription(description);
    tx.setReference(reference);
    tx.setModePaiement(modePaiement);
    tx.setUserId(userId);

    if (gardienId != null) {
        tx.setGardien(gardienRepository.findById(gardienId)
                .orElseThrow());
    }

    if (clientId != null) {
        tx.setClient(clientRepository.findById(clientId)
                .orElseThrow());
    }

    // 1️⃣ Caisse + Transaction (comme déjà fait)
      TransactionCaisse savedTx = transactionRepository.save(tx);

    // 2️⃣ Historique métier (ICI seulement)
        enregistrerHistoriqueMetier(savedTx, categorie);

    return savedTx;
}

private void enregistrerHistoriqueMetier(
        TransactionCaisse tx,
        CategorieOperation categorie
) {

    /* ======================
       👨‍✈️ HISTORIQUE AGENT
       ====================== */
    if (tx.getGardien() != null) {

        AgentFinanceHistory history = new AgentFinanceHistory();
        history.setGardien(tx.getGardien());
        history.setTransactionCaisse(tx);
        history.setMontant(tx.getMontant());
        history.setDate(tx.getDateTransaction());
        history.setType(categorie.name()); // SALAIRE / AVANCE / PRET

        agentFinanceHistoryRepository.save(history);
    }

    /* ======================
       👥 HISTORIQUE CLIENT
       ====================== */
    if (tx.getClient() != null) {

        CustomerFinanceHistory history = new CustomerFinanceHistory();
        history.setClient(tx.getClient());
        history.setTransactionCaisse(tx);
        history.setMontant(tx.getMontant());
        history.setDatePaiement(tx.getDateTransaction());
        history.setType(categorie.name()); // FACTURE / PAIEMENT / REMBOURSEMENT

        // 📄 Lien facture si nécessaire
        if (categorie == CategorieOperation.FACTURE && tx.getReference() != null) {
            Facture facture = factureRepository
                    .findById(extraireIdFacture(tx.getReference()))
                    .orElse(null);
            history.setFacture(facture);
        }

        customerFinanceHistoryRepository.save(history);
    }
}
private Long extraireIdFacture(String reference) {
    try {
        return Long.parseLong(reference.replace("FACT-", ""));
    } catch (Exception e) {
        return null;
    }
}


}