package com.king.peace.Utiltys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.ImplementServices.FacturationService;
import lombok.RequiredArgsConstructor;

import jakarta.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class FacturationScheduler {

    private FacturationService facturationService;
    private final FactureRepository factureRepository;
    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;

     /**
     * Scheduler automatique : génère une facture pour chaque contrat actif.
     * Test rapide : toutes les 30 secondes
     */
    // @Scheduled(cron = "*/30 * * * * *") 
    // @Transactional
    // public void generateFacturesAutomatiques() {
    //     System.out.println("Scheduler exécuté : génération de factures à " + java.time.LocalDateTime.now());

    //     // Récupérer tous les contrats actifs
    //     List<Contrats> contratsActifs = contratRepository.findByActiveTrue();

    //     if (contratsActifs.isEmpty()) {
    //         System.out.println("Aucun contrat actif trouvé.");
    //         return;
    //     }

    //     for (Contrats contrat : contratsActifs) {
    //         Client client = contrat.getClient();
    //         if (client == null) continue;

    //         // Vérifier si une facture a déjà été générée pour ce contrat aujourd'hui
    //         boolean factureExist = factureRepository.existsByContratsIdAndDateEmission(contrat.getId(), LocalDate.now());
    //         if (factureExist) continue;

    //         // Création de la facture
    //         Facture facture = Facture.builder()
    //                 .client(client)
    //                 .contrats(contrat)
    //                 .dateEmission(LocalDate.now())
    //                 .nombreGardiens(contrat.getNombreGardiens())
    //                 .montantParGardien(contrat.getMontantParGardien())
    //                 .nombreJours(contrat.getNombreJoursMensuel())
    //                 .devise(contrat.getDevise())
    //                 .remise(0)
    //                 .description("Facture automatique")
    //                 .montantTotal(contrat.getNombreGardiens() * contrat.getMontantParGardien())
    //                 .statut(StatutFacture.NEW)  // <- obligatoire
    //                 .refFacture("AUTO-" + System.currentTimeMillis())
    //                 .build();

    //         factureRepository.save(facture);

    //         // Historique
    //         CustomerFinanceHistory history = CustomerFinanceHistory.builder()
    //                 .client(client)
    //                 .facture(facture)
    //                 .type("FACTURE_AUTO")
    //                 .montant(facture.getMontantTotal())
    //                 .build();
    //         historyRepository.save(history);

    //         System.out.println("Facture générée pour le contrat " + contrat.getId() + " -> " + facture.getRefFacture());
    //     }
    



     @Scheduled(cron = "0 0 0 * * ?") // tous les jours à minuit
    public void generateDailyFactures() {
        facturationService.generateFacturesAutomatiques();
    } 
    

}