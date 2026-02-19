package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.CustomerFinanceHistoryRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dao.TauxJournalierRepository;
import com.king.peace.Dto.FactureDTO;
import com.king.peace.Dto.UpdateFactureRequest;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.CustomerFinanceHistory;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.Entitys.StatutGardien;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturationService {

    private final ContratRepository contratRepository;
    private final FactureRepository factureRepository;
    private final TauxJournalierRepository tauxJournalierRepository;
    private final CustomerFinanceHistoryRepository historyRepository;
    private final ClientService clientService;
    private final ClientRepository clientRepository;        // <-- injecté ici




     // Méthode appelée périodiquement (via Scheduler ou Cron)
    @Transactional
    public void generateFacturesAutomatiques() {
        LocalDate today = LocalDate.now();
        List<Contrats> contratsActifs = contratRepository.findByStatut(StatutGardien.ACTIF);

        for (Contrats contrat : contratsActifs) {

            LocalDate nextCycleDate = computeNextCycleDate(contrat);
             // pas prêt
            if (nextCycleDate == null || today.isBefore(nextCycleDate)) continue;
             // éviter doublon (si cron tourne plusieurs fois)
             if (factureRepository.existsByContratsAndDateCycle(contrat, nextCycleDate)) continue;


            if (shouldGenerateFacture(contrat, today)) {
                 Facture facture = Facture.builder()
                .client(contrat.getClient())
                .contrats(contrat)
                .dateEmission(today)
                .nombreGardiens(contrat.getNombreGardiens())
                .montantParGardien(contrat.getMontantParGardien())
                .nombreJours(contrat.getNombreJoursMensuel())
                .montantTotal(contrat.getNombreGardiens() * contrat.getMontantParGardien()) // ⚠️ à ajuster si tarif/jour
                .devise(contrat.getDevise())
                .statut(StatutFacture.NEW)
                .refFacture(generateReference())
                .dateCycle(nextCycleDate) // ✅ la vraie date planifiée
                .description("Facture automatique pour contrat " + contrat.getId())
                .build();
                
                
                factureRepository.save(facture);

                // Historique initial
                CustomerFinanceHistory history = CustomerFinanceHistory.builder()
                        .client(contrat.getClient())
                        .facture(facture)
                        .type("FACTURATION")
                        .montant(facture.getMontantTotal())
                        .active(true)
                        .build();

                historyRepository.save(history);
            }
        }
    }

    /** Calcule la prochaine dateCycle (planifiée) */
private LocalDate computeNextCycleDate(Contrats contrat) {

    LocalDate start = contrat.getDateDebutFacturation();
    if (start == null) return null;

    Integer cycleDays = contrat.getNombreJoursMensuel();

    int days = (cycleDays != null && cycleDays > 0)
            ? cycleDays
            : 30; // valeur par défaut

    return factureRepository.findTopByContratsOrderByDateCycleDesc(contrat)
            .map(f -> f.getDateCycle().plusDays(days))
            .orElse(start);
}


    private boolean shouldGenerateFacture(Contrats contrat, LocalDate today) {
        LocalDate lastBillingDate = factureRepository.findLastByContrats(contrat)
                .map(Facture::getDateEmission)
                .orElse(contrat.getDateDebutFacturation());
        return ChronoUnit.DAYS.between(lastBillingDate, today) >= contrat.getNombreJoursMensuel();
    }

    private String generateReference() {
        return "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


@Transactional
public UpdateFactureRequest updateFacture(Long factureId, UpdateFactureRequest req) {

    Facture f = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture introuvable"));

    // 🔒 Sécurité: on modifie seulement si NEW
    if (f.getStatut() != StatutFacture.NEW) {
        throw new IllegalStateException("cette facture semble etre déjà emis.");
    }

    // ✅ Appliquer modifications (si valeurs envoyées)
    if (req.getNombreGardiens() != null) {
        f.setNombreGardiens(req.getNombreGardiens());
    }
    if (req.getMontantParGardien() != null) {
        f.setMontantParGardien(req.getMontantParGardien());
    }
    if (req.getDiscount() != null) {
        f.setRemise(req.getDiscount());
    }
    if (req.getNotes() != null) {
        f.setDescription(req.getNotes());
    }

    // ✅ Recalcul du total
    recalcTotal(f);

    // ✅ Confirmation => EMIS
    if (Boolean.TRUE.equals(req.getConfirm())) {
        f.setStatut(StatutFacture.EMIS);
        f.setDateEmission(LocalDate.now()); // ou dateEmis si tu préfères

  CustomerFinanceHistory ex = historyRepository.findByFactureAndType(f, "FACTURE_AUTO");

if (ex != null) {
    ex.setMontant(f.getMontantTotal());
    ex.setActive(false);
    ex.setType("EMISSION_FACTURE"); // si tu veux changer le type
    historyRepository.save(ex);
}
// sinon: rien
    }
    return req;
}

private void recalcTotal(Facture f) {
    // Exemple : total = nbGardiens * montantParGardien * nbJours - discount
    // Ajuste selon ta logique (par mois ou par jour)
    double base = f.getNombreGardiens() * f.getMontantParGardien();

    double discount = (f.getRemise() == 0) ? 0 : f.getRemise();
    double total = Math.max(0, base - discount);

    f.setMontantTotal(total);
}

private FactureDTO toDto(Facture facture) {
    return FactureDTO.builder()
            .id(facture.getId())
            .dateEmission(facture.getDateEmission())
            .montantTotal(facture.getMontantTotal())
            .statut(facture.getStatut() != null ? facture.getStatut().name() : null)
            .description(facture.getDescription())
            .refFacture(facture.getRefFacture())
            .nombreGardiens(facture.getNombreGardiens())
            .montantParGardien(facture.getMontantParGardien())
            .nombreJours(facture.getNombreJours())
            .devise(facture.getDevise() != null ? facture.getDevise().name() : null)
            .remise(facture.getRemise())
            .commentaire(facture.getCommentaire())
            .clientId(facture.getClient() != null ? facture.getClient().getId() : null)
            .contratId(facture.getContrats() != null ? facture.getContrats().getId() : null)
            .factureOrigineId(facture.getFactureOrigine() != null ? facture.getFactureOrigine().getId() : null)
            .build();
}


@Transactional
public FactureDTO createManualFacture(FactureDTO dto) {
    Facture facture = Facture.builder()
            .client(dto.getClientId() != null ? clientRepository.findById(dto.getClientId()).orElse(null) : null)
            .contrats(dto.getContratId() != null ? contratRepository.findById(dto.getContratId()).orElse(null) : null)
            .dateEmission(dto.getDateEmission() != null ? dto.getDateEmission() : LocalDate.now())
            .nombreGardiens(dto.getNombreGardiens())
            .montantParGardien(dto.getMontantParGardien())
            .nombreJours(dto.getNombreJours())
            .devise(dto.getDevise() != null ? Devise.valueOf(dto.getDevise()) : Devise.CDF)
            .description(dto.getDescription())
            .remise(dto.getRemise())
            .commentaire(dto.getCommentaire())
            .montantTotal(calculateMontantTotal(dto.getNombreGardiens(), dto.getMontantParGardien(), dto.getRemise()))
            .statut(StatutFacture.NEW)
            .refFacture(generateReference())
            .build();

    factureRepository.save(facture);

    // Historique
    CustomerFinanceHistory history = CustomerFinanceHistory.builder()
            .client(facture.getClient())
            .facture(facture)
            .type("FACTURE_MANUELLE")
            .montant(facture.getMontantTotal())
            .build();
    historyRepository.save(history);

    return toDto(facture);
}

private double calculateMontantTotal(int nombreGardiens, double montantParGardien, double remise) {
    return (nombreGardiens * montantParGardien) - remise;
}

@Transactional
public FactureDTO createAvoir(Long factureOrigineId, FactureDTO dto) {
    Facture factureOrigine = factureRepository.findById(factureOrigineId)
            .orElseThrow(() -> new RuntimeException("Facture origine non trouvée"));

    // On crée une nouvelle facture avec montant négatif si c’est un avoir
    Facture avoir = Facture.builder()
            .client(factureOrigine.getClient())
            .contrats(factureOrigine.getContrats())
            .dateEmission(LocalDate.now())
            .nombreGardiens(factureOrigine.getNombreGardiens())
            .montantParGardien(factureOrigine.getMontantParGardien())
            .nombreJours(factureOrigine.getNombreJours())
            .devise(factureOrigine.getDevise())
            .description(dto.getDescription() != null ? dto.getDescription() : "Avoir pour facture " + factureOrigine.getRefFacture())
            .remise(dto.getRemise())
            .commentaire(dto.getCommentaire())
            .montantTotal(-(factureOrigine.getMontantTotal() - dto.getRemise())) // montant négatif
            .statut(StatutFacture.NEW)
            .refFacture(generateReference())
            .factureOrigine(factureOrigine)
            .build();

    factureRepository.save(avoir);

    // Historique
    CustomerFinanceHistory history = CustomerFinanceHistory.builder()
            .client(avoir.getClient())
            .facture(avoir)
            .type("AVOIR")
            .montant(avoir.getMontantTotal())
            .build();
    historyRepository.save(history);

    return toDto(avoir);
}

public List<Map<String, Object>> monthlyStats(LocalDate dateFrom, LocalDate dateTo, Long clientId) {

  List<Object[]> rows = factureRepository.monthlyStats(dateFrom, dateTo, clientId);

  // init 12 mois à 0
  double[] totals = new double[12];
  Arrays.fill(totals, 0.0);

  // rows: [month(1..12), total]
  for (Object[] r : rows) {
    int month = ((Number) r[0]).intValue(); // 1..12
    double total = ((Number) r[1]).doubleValue();
    if (month >= 1 && month <= 12) totals[month - 1] = total;
  }

  List<Map<String, Object>> result = new ArrayList<>();
  for (int m = 1; m <= 12; m++) {
    result.add(Map.of("month", m, "total", totals[m - 1]));
  }
  return result;
}
    }
