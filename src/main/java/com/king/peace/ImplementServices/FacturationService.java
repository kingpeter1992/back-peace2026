package com.king.peace.ImplementServices;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dao.TauxJournalierRepository;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.Entitys.TauxJournalier;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturationService {

    private final ContratRepository contratRepository;
    private final FactureRepository factureRepository;
    private final EmailServiceImpl notificationService;

 // Génération automatique des factures
    public void genererFacturesAutomatiques() {
        LocalDate today = LocalDate.now();

        contratRepository.findAll().stream()
                .filter(c -> c.getDateFin() == null || !c.getDateFin().isBefore(today))
                .forEach(contrat -> {
                    long joursEcoules = java.time.temporal.ChronoUnit.DAYS.between(
                            contrat.getDateDebutFacturation(), today
                    );

                    if (joursEcoules >= contrat.getNombreJoursMensuel()) {

                      //  double dernierTaux = getDernierTauxPourContrat(contrat);
                        Facture facture = createFactureAutomatique(contrat, joursEcoules, 0);

                        factureRepository.save(facture);

                        // Mise à jour de la date de début de facturation
                        // Notification automatique 
                        notificationService.envoyerFacture(contrat.getClient(), facture, false);
                        contrat.setDateDebutFacturation(today.plusDays(1));
                        contratRepository.save(contrat);
                    }
                });
    }

    // Récupère le dernier taux journalier pour le gardien du contrat
    // private double getDernierTauxPourContrat(Contrats contrat) {
    //     return contrat.getGardiens().stream()
    //             .mapToDouble(g -> tauxJournalierRepository.findByGardienId(g.getId())
    //                     .map(TauxJournalier::getMontant)
    //                     .orElse(0.0))
    //             .sum(); // ou moyenne si tu veux
    // }

    // Création de facture automatique
    private Facture createFactureAutomatique(Contrats contrat, long jours, double taux) {
        double montantTotal = taux * contrat.getNombreGardiens() * jours;

        String description = String.format(
                "Facturation automatique du contrat %s\n" +
                "Nombre de gardiens : %d\n" +
                "Taux journalier par gardien : %.2f %s\n" +
                "Nombre de jours : %d\n" +
                "Montant total : %.2f %s",
                contrat.getRefContrats(),
                contrat.getNombreGardiens(),
                taux,
                contrat.getDevise(),
                jours,
                montantTotal,
                contrat.getDevise()
        );

        return Facture.builder()
                .contrats(contrat)
                .refFacture("FAC-" + System.currentTimeMillis())
                .nombreGardiens(contrat.getNombreGardiens())
                .montantParGardien(taux)
                .nombreJours((int) jours)
                .montantTotal(montantTotal)
                .devise(contrat.getDevise())
                .description(description)
                .dateEmission(LocalDate.now())
                .statut(StatutFacture.EMIS)
                .build();
    }

    // Refacturation (utilise le taux initial et la devise initiale)
    public Facture refacturer(Long factureId, double montantCorrection, String remarque) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));

        // on conserve le montantParGardien et devise initiale
        facture.setMontantTotal(facture.getMontantTotal() + montantCorrection);
        facture.setDescription(facture.getDescription() + "\nAjustement: " + remarque);
        facture.setStatut(StatutFacture.REFACTURE);
 // Notification automatique pour la refacturation
        notificationService.envoyerFacture(facture.getContrats().getClient(), facture, true);

        return factureRepository.save(facture);
    }

    // Annulation (reprend les valeurs initiales de la première facture)
    public Facture annulerFacture(Long factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));

        facture.setStatut(StatutFacture.ANNULE);
        facture.setMontantTotal(0);
        facture.setDescription(facture.getDescription() + "\nFacture annulée");

        return factureRepository.save(facture);
    }
    }
