package com.king.peace.ImplementServices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.king.peace.Dao.CaisseRepository;
import com.king.peace.Dao.CaisseSessionRepository;
import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.FacturePaiementRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.RepositoryAgentFinanceHistory;
import com.king.peace.Dao.TauxJournalierRepository;
import com.king.peace.Dao.TransactionCaisseRepository;
import com.king.peace.Dto.ApplyPaiementDTO;
import com.king.peace.Dto.CaisseSessionDto;
import com.king.peace.Dto.CaisseSummaryDTO;
import com.king.peace.Dto.CloturerCaisseDTO;
import com.king.peace.Dto.CustomerFinanceHistoryDto;
import com.king.peace.Dto.DecaissementAgentDTO;
import com.king.peace.Dto.DepenseDTO;
import com.king.peace.Dto.EncaissementAgentDTO;
import com.king.peace.Dto.EncaissementClientDTO;
import com.king.peace.Dto.OperationCaisseDTO;
import com.king.peace.Dto.OuvrirCaisseDTO;
import com.king.peace.Dto.RemboursementClientDTO;
import com.king.peace.Dto.SessionReportDTO;
import com.king.peace.Dto.SoldeCaisseDTO;
import com.king.peace.Dto.TransactionCaisseDto;
import com.king.peace.Dto.TxReportDTO;
import com.king.peace.Dto.Response.FactureMiniDto;
import com.king.peace.Dto.Response.LettrageContextDto;
import com.king.peace.Dto.Response.LettrerLine;
import com.king.peace.Dto.Response.LettrerManyRequest;
import com.king.peace.Dto.Response.LettrerManyResult;
import com.king.peace.Entitys.AgentFinanceHistory;
import com.king.peace.Entitys.Caisse;
import com.king.peace.Entitys.CaisseSession;
import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.FacturePaiement;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.ModePaiement;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.Entitys.StatutSessionCaisse;
import com.king.peace.Entitys.TauxJournalier;
import com.king.peace.Entitys.TransactionCaisse;
import com.king.peace.Entitys.TypeTransaction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CaisseService {

    private final TransactionCaisseRepository transactionRepository;
    private final GardienRepository gardienRepository;
    private final RepositoryAgentFinanceHistory agentFinanceHistoryRepository;
    private final ClientRepository clientRepository;
    private final FactureRepository factureRepository;
    private final CaisseSessionRepository caisseSessionRepository;
    private final TauxJournalierRepository tauxJournalierRepository;
    private final FacturePaiementRepository facturePaiementRepository;



    @Transactional
    public CaisseSession ouvrirCaisse(OuvrirCaisseDTO dto, String userId) {

        caisseSessionRepository.findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .ifPresent(s -> {
                    throw new RuntimeException("Une caisse est déjà ouverte aujourd'hui");
                });

        CaisseSession s = new CaisseSession();
        s.setDateJour(LocalDate.now());
        s.setStatut(StatutSessionCaisse.OUVERTE);
        s.setDateOuverture(LocalDateTime.now());
        s.setOpenedBy(userId);
        s.setSoldeInitialUSD(dto.getSoldeInitialUSD());
        s.setSoldeInitialCDF(dto.getSoldeInitialCDF());
        s.setSoldeActuelUSD(dto.getSoldeInitialUSD());
        s.setSoldeActuelCDF(dto.getSoldeInitialCDF());
        s.setNoteOuverture(dto.getNote());

        TauxJournalier tx = new TauxJournalier();

        tx.setDate(LocalDate.now());
        tx.setTaux(dto.getTauxChange());
        tauxJournalierRepository.save(tx);

        return caisseSessionRepository.save(s);
    }

    @Transactional
    public CaisseSession cloturerCaisse(CloturerCaisseDTO dto, String userId) {

        CaisseSession s = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Aucune caisse ouverte aujourd'hui"));

        s.setStatut(StatutSessionCaisse.FERMEE);
        s.setDateCloture(LocalDateTime.now());
        s.setClosedBy(userId);
        s.setNoteCloture(dto.getNote());

        return caisseSessionRepository.save(s);
    }

    private void enregistrerHistoriqueMetier(TransactionCaisse tx, CategorieOperation category) {
        double signedAmount = (tx.getType() == TypeTransaction.ENCAISSEMENT)
                ? tx.getMontant()
                : -tx.getMontant();
        /*
         * ======================
         * 👨‍✈️ HISTORIQUE AGENT
         * ======================
         */
        if (tx.getGardien() != null) {

            AgentFinanceHistory history = new AgentFinanceHistory();
            Gardien g = gardienRepository.getById(tx.getGardien().getId());

            history.setGardien(tx.getGardien());
            history.setTransactionCaisse(tx);
            history.setDate(tx.getDateTransaction());
            history.setDevise(tx.getDevise());
            history.setType(category.name()); // SALAIRE / AVANCE / PRET

            double montantFinal = signedAmount;
            // Sécuriser null + comparaison correcte String
            Devise from = tx.getDevise();
            Devise to = g.getDevise();
            if (from == null || to == null) {
                throw new RuntimeException("Devise manquante (tx ou history)");
            }

            if (to != from) {
                // récupérer le dernier taux (ou le taux de la session si tu l’as)
                TauxJournalier taux = tauxJournalierRepository
                        .findTopByOrderByDateDesc()
                        .orElseThrow(() -> new RuntimeException("Aucun taux trouvé"));

                montantFinal = convertir(
                        signedAmount, // ✅ montant signé
                        from, // from
                        to, // to
                        taux.getTaux());
            }

            history.setMontant(montantFinal);

            agentFinanceHistoryRepository.save(history);
        }

        /*
         * ======================
         * 👥 HISTORIQUE CLIENT
         * ======================
         */
       
    }

    private double convertir(double montant, Devise from, Devise to, double taux) {

        if (from == null || to == null) {
            throw new RuntimeException("Devise null");
        }
        if (taux <= 0) {
            throw new RuntimeException("Taux invalide");
        }

        if (from == to)
            return montant;

        // USD -> CDF
        if (from == Devise.USD && to == Devise.CDF) {
            return montant * taux;
        }

        // CDF -> USD
        if (from == Devise.CDF && to == Devise.USD) {
            return montant / taux;
        }

        throw new RuntimeException("Devise non supportée: " + from + " -> " + to);
    }

    public CaisseSessionDto getSessionOuverteDuJour() {

        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Aucune caisse ouverte aujourd'hui"));

        return CaisseSessionDto.builder()
                .id(session.getId())
                .dateJour(session.getDateJour())
                .statut(session.getStatut())
                .dateOuverture(session.getDateOuverture())
                .dateCloture(session.getDateCloture())
                .soldeInitialUSD(session.getSoldeInitialUSD())
                .soldeInitialCDF(session.getSoldeInitialCDF())
                .soldeActuelUSD(session.getSoldeActuelUSD())
                .soldeActuelCDF(session.getSoldeActuelCDF())
                .openedBy(session.getOpenedBy())
                .closedBy(session.getClosedBy())
                .noteOuverture(session.getNoteOuverture())
                .noteCloture(session.getNoteCloture())
                .build();
    }

    // @Transactional(readOnly = true)
    public List<TransactionCaisseDto> historiqueDuJour() {

        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Aucune caisse ouverte aujourd'hui"));

        return transactionRepository.findBySessionIdOrderByDateTransactionDesc(session.getId())
                .stream()
                .map(tx -> TransactionCaisseDto.builder()
                        .id(tx.getId())
                        .dateTransaction(tx.getDateTransaction())
                        .type(tx.getType())
                        .category(tx.getCategory())
                        .devise(tx.getDevise())
                        .modePaiement(tx.getModePaiement())
                        .montant(tx.getMontant())
                        .sens(tx.getSens())
                        .description(tx.getDescription())
                        .reference(tx.getReference())
                        .soldeAvant(tx.getSoldeAvant())
                        .soldeApres(tx.getSoldeApres())
                        .userId(tx.getUserId())

                        .clientId(tx.getClient() != null ? tx.getClient().getId() : null)
                        .nomClient(tx.getClient() != null ? tx.getClient().getNom() : null)

                        .gardienId(tx.getGardien() != null ? tx.getGardien().getId() : null)
                        .nomGardien(tx.getGardien() != null ? tx.getGardien().getNom() : null)
                        .build())
                .toList();
    }

    public CaisseSummaryDTO buildSummary(LocalDate from, LocalDate to) {

        List<TransactionCaisse> txs = transactionRepository.findByDateRange(
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay());

        double encUSD = 0, decUSD = 0, encCDF = 0, decCDF = 0;

        for (TransactionCaisse t : txs) {

            if (t.getDevise() == Devise.USD) {
                if (t.getType() == TypeTransaction.ENCAISSEMENT)
                    encUSD += t.getMontant();
                else
                    decUSD += t.getMontant();
            }

            if (t.getDevise() == Devise.CDF) {
                if (t.getType() == TypeTransaction.ENCAISSEMENT)
                    encCDF += t.getMontant();
                else
                    decCDF += t.getMontant();
            }
        }

        return new CaisseSummaryDTO(encUSD, decUSD, encCDF, decCDF);
    }

    public List<TxReportDTO> findByDateRange(LocalDateTime from, LocalDateTime to) {

        List<TransactionCaisse> operations = transactionRepository.findByDateRange(from, to);

        return operations.stream()
                .map(t -> new TxReportDTO(
                        t.getId(),
                        t.getDateTransaction(),
                        t.getReference(),
                        t.getCategory() != null ? t.getCategory().name() : null,
                        t.getType() != null ? t.getType().name() : null,
                        t.getDevise() != null ? t.getDevise().name() : null,
                        t.getModePaiement() != null ? t.getModePaiement().name() : null,
                        t.getMontant(),
                        t.getSoldeAvant(),
                        t.getSoldeApres(),
                        t.getDescription(),
                        t.getSession() != null ? t.getSession().getId() : null,
                        t.getClient() != null ? t.getClient().getId() : null,
                        t.getGardien() != null ? t.getGardien().getId() : null))
                .toList();
    }

    @Transactional
    public TransactionCaisse encaisserClientPro(OperationCaisseDTO dto, String username) {

        // =========================
        // 0) VALIDATIONS
        // =========================
        if (dto == null)
            throw new RuntimeException("Payload vide");
        if (dto.getType() == null)
            throw new RuntimeException("Type transaction obligatoire");
        if (dto.getDevise() == null)
            throw new RuntimeException("Devise obligatoire");
        if (dto.getModePaiement() == null)
            throw new RuntimeException("Mode de paiement obligatoire");
        if (dto.getMontant() <= 0)
            throw new RuntimeException("Montant invalide");

        // ✅ category = classification (pas de logique métier)
        if (dto.getCategory() == null) {
            throw new RuntimeException("Catégorie obligatoire"); // ou tu mets une valeur par défaut
        }

        // =========================
        // 1) SESSION OUVERTE + LOCK
        // =========================
        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Caisse fermée. Ouvre la caisse d'abord."));

        session = caisseSessionRepository.lockById(session.getId())
                .orElseThrow(() -> new RuntimeException("Session caisse introuvable"));

        // =========================
        // 2) LIEN : client OU gardien OU personne
        // =========================
        final Long clientId = (dto.getClientId() != null && dto.getClientId() > 0) ? dto.getClientId() : null;

        Client client = null;
        if (clientId != null) {
            client = clientRepository.findByIdNative(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client introuvable: " + clientId));
        }

        // =========================
        // 3) SOLDE AVANT / INSUFFISANCE
        // =========================
        double soldeAvant = (dto.getDevise() == Devise.USD)
                ? session.getSoldeActuelUSD()
                : session.getSoldeActuelCDF();

        final double EPS = 0.0001;
        if (dto.getType() == TypeTransaction.DECAISSEMENT && (soldeAvant + EPS) < dto.getMontant()) {
            throw new RuntimeException("Solde insuffisant en " + dto.getDevise()
                    + " (Disponible: " + soldeAvant + ")");
        }

        // =========================
        // 4) SOLDE APRES + MAJ SESSION
        // =========================
        double soldeApres = (dto.getType() == TypeTransaction.ENCAISSEMENT)
                ? soldeAvant + dto.getMontant()
                : soldeAvant - dto.getMontant();

        soldeApres = Math.round(soldeApres * 100.0) / 100.0;
        if (dto.getDevise() == Devise.USD)
            session.setSoldeActuelUSD(soldeApres);
        else
            session.setSoldeActuelCDF(soldeApres);
        caisseSessionRepository.save(session);

        // =========================
        // 5) CREER TRANSACTION
        // =========================
        TransactionCaisse tx = new TransactionCaisse();
        tx.setSession(session);
        tx.setDateTransaction(LocalDateTime.now());
        tx.setUserId(username);

        tx.setType(dto.getType());
        tx.setDevise(dto.getDevise());
        tx.setMontant(dto.getMontant());
        tx.setModePaiement(dto.getModePaiement());

        // ✅ category uniquement pour classification
        tx.setCategory(dto.getCategory());

        tx.setDescription(dto.getDescription() == null ? "" : dto.getDescription());

        String ref = dto.getReference();
        if (ref == null || ref.isBlank())
            ref = "TX-" + System.currentTimeMillis();
        tx.setReference(ref);

        tx.setSoldeAvant(soldeAvant);
        tx.setSoldeApres(soldeApres);
        tx.setSens(dto.getType() == TypeTransaction.ENCAISSEMENT ? "+" : "-");

        if (client != null)
            tx.setClient(client);

        TransactionCaisse saved = transactionRepository.save(tx);

        // =========================
        // 6) HISTORIQUE METIER
        // =========================
        if (saved.getClient() != null || saved.getGardien() != null) {
            // factureId => plus géré ici => null
            enregistrerHistoriqueMetier(saved, saved.getCategory());
        }

        return saved;
    }

    @Transactional
    public TransactionCaisse enregistrerOperationGardien(OperationCaisseDTO dto, String username) {

        // 0) validations communes
        if (dto == null)
            throw new RuntimeException("Payload vide");
        if (dto.getType() == null)
            throw new RuntimeException("Type transaction obligatoire");
        if (dto.getDevise() == null)
            throw new RuntimeException("Devise obligatoire");
        if (dto.getCategory() == null)
            throw new RuntimeException("Catégorie obligatoire");
        if (dto.getModePaiement() == null)
            throw new RuntimeException("Mode de paiement obligatoire");
        if (dto.getMontant() <= 0)
            throw new RuntimeException("Montant invalide");

        // 1) validations gardien
        final Long gardienId = (dto.getGardienId() != null && dto.getGardienId() > 0) ? dto.getGardienId() : null;
        if (gardienId == null)
            throw new RuntimeException("Gardien obligatoire");

        // (optionnel) imposer catégories
        if (dto.getCategory() != CategorieOperation.AVANCE
                && dto.getCategory() != CategorieOperation.PRET
                && dto.getCategory() != CategorieOperation.SALAIRE) {
            throw new RuntimeException("Catégorie gardien invalide: " + dto.getCategory());
        }

        // 2) session + lock
        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Caisse fermée. Ouvre la caisse d'abord."));
        session = caisseSessionRepository.lockById(session.getId())
                .orElseThrow(() -> new RuntimeException("Session caisse introuvable"));

        // 3) charger gardien
        Gardien gardien = gardienRepository.findById(gardienId)
                .orElseThrow(() -> new RuntimeException("Gardien introuvable: " + gardienId));

        if (gardien.getDevise() == null || dto.getDevise() == null
                || !gardien.getDevise().equals(dto.getDevise())) {

            throw new RuntimeException(
                    "Devise non compatible. Gardien en " + gardien.getDevise()
                            + " mais opération en " + dto.getDevise());
        }
        // 4) solde avant + insuffisance
        double soldeAvant = (dto.getDevise() == Devise.USD)
                ? session.getSoldeActuelUSD()
                : session.getSoldeActuelCDF();

        final double EPS = 0.0001;
        if (dto.getType() == TypeTransaction.DECAISSEMENT && (soldeAvant + EPS) < dto.getMontant()) {
            throw new RuntimeException("Solde insuffisant en " + dto.getDevise() + " (Disponible: " + soldeAvant + ")");
        }

        // 5) solde après + maj session
        double soldeApres = (dto.getType() == TypeTransaction.ENCAISSEMENT)
                ? soldeAvant + dto.getMontant()
                : soldeAvant - dto.getMontant();

        soldeApres = Math.round(soldeApres * 100.0) / 100.0;

        if (dto.getDevise() == Devise.USD)
            session.setSoldeActuelUSD(soldeApres);
        else
            session.setSoldeActuelCDF(soldeApres);

        caisseSessionRepository.save(session);

        // 6) tx
        TransactionCaisse tx = new TransactionCaisse();
        tx.setSession(session);
        tx.setDateTransaction(LocalDateTime.now());
        tx.setUserId(username);

        tx.setType(dto.getType());
        tx.setCategory(dto.getCategory());
        tx.setDevise(dto.getDevise());
        tx.setMontant(dto.getMontant());
        tx.setModePaiement(dto.getModePaiement());
        tx.setDescription(dto.getDescription() == null ? "" : dto.getDescription());

        String ref = dto.getReference();
        if (ref == null || ref.isBlank())
            ref = dto.getCategory().name() + "-" + System.currentTimeMillis();
        tx.setReference(ref);

        tx.setSoldeAvant(soldeAvant);
        tx.setSoldeApres(soldeApres);
        tx.setSens(dto.getType() == TypeTransaction.ENCAISSEMENT ? "+" : "-");

        tx.setGardien(gardien);

        TransactionCaisse saved = transactionRepository.save(tx);

        // 7) historique gardien
        enregistrerHistoriqueMetier(saved, saved.getCategory());

        return saved;
    }

    @Transactional
    public TransactionCaisse enregistrerOperationAutre(OperationCaisseDTO dto, String username) {

        // 0) validations communes
        if (dto == null)
            throw new RuntimeException("Payload vide");
        if (dto.getType() == null)
            throw new RuntimeException("Type transaction obligatoire");
        if (dto.getDevise() == null)
            throw new RuntimeException("Devise obligatoire");
        if (dto.getCategory() == null)
            throw new RuntimeException("Catégorie obligatoire");
        if (dto.getModePaiement() == null)
            throw new RuntimeException("Mode de paiement obligatoire");
        if (dto.getMontant() <= 0)
            throw new RuntimeException("Montant invalide");

        // 1) session + lock
        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseThrow(() -> new RuntimeException("Caisse fermée. Ouvre la caisse d'abord."));
        session = caisseSessionRepository.lockById(session.getId())
                .orElseThrow(() -> new RuntimeException("Session caisse introuvable"));

        // 2) ids optionnels
        final Long clientId = (dto.getClientId() != null && dto.getClientId() > 0) ? dto.getClientId() : null;
        final Long gardienId = (dto.getGardienId() != null && dto.getGardienId() > 0) ? dto.getGardienId() : null;

        if (clientId != null && gardienId != null) {
            throw new RuntimeException("Transaction invalide : client et gardien à la fois");
        }

        Client client = null;
        Gardien gardien = null;

        if (clientId != null) {
            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client introuvable: " + clientId));
        }
        if (gardienId != null) {
            gardien = gardienRepository.findById(gardienId)
                    .orElseThrow(() -> new RuntimeException("Gardien introuvable: " + gardienId));
        }

        // 3) solde avant + insuffisance
        double soldeAvant = (dto.getDevise() == Devise.USD)
                ? session.getSoldeActuelUSD()
                : session.getSoldeActuelCDF();

        final double EPS = 0.0001;
        if (dto.getType() == TypeTransaction.DECAISSEMENT && (soldeAvant + EPS) < dto.getMontant()) {
            throw new RuntimeException("Solde insuffisant en " + dto.getDevise() + " (Disponible: " + soldeAvant + ")");
        }

        // 4) solde après + maj session
        double soldeApres = (dto.getType() == TypeTransaction.ENCAISSEMENT)
                ? soldeAvant + dto.getMontant()
                : soldeAvant - dto.getMontant();

        soldeApres = Math.round(soldeApres * 100.0) / 100.0;

        if (dto.getDevise() == Devise.USD)
            session.setSoldeActuelUSD(soldeApres);
        else
            session.setSoldeActuelCDF(soldeApres);

        caisseSessionRepository.save(session);

        // 5) tx
        TransactionCaisse tx = new TransactionCaisse();
        tx.setSession(session);
        tx.setDateTransaction(LocalDateTime.now());
        tx.setUserId(username);

        tx.setType(dto.getType());
        tx.setCategory(dto.getCategory());
        tx.setDevise(dto.getDevise());
        tx.setMontant(dto.getMontant());
        tx.setModePaiement(dto.getModePaiement());
        tx.setDescription(dto.getDescription() == null ? "" : dto.getDescription());

        String ref = dto.getReference();
        if (ref == null || ref.isBlank())
            ref = dto.getCategory().name() + "-" + System.currentTimeMillis();
        tx.setReference(ref);

        tx.setSoldeAvant(soldeAvant);
        tx.setSoldeApres(soldeApres);
        tx.setSens(dto.getType() == TypeTransaction.ENCAISSEMENT ? "+" : "-");

        if (client != null)
            tx.setClient(client);
        if (gardien != null)
            tx.setGardien(gardien);

        TransactionCaisse saved = transactionRepository.save(tx);

        // 6) historique si client ou gardien
        if (saved.getClient() != null || saved.getGardien() != null) {
            enregistrerHistoriqueMetier(saved, saved.getCategory());
        }

        return saved;
    }


    public double creditDisponible(Long clientId, Devise devise) {
  double enc = transactionRepository.sumMontantByClientAndDeviseAndType(clientId, devise, TypeTransaction.ENCAISSEMENT);
  double aff = facturePaiementRepository.sumAffecteByClientAndDevise(clientId, devise);
  return Math.max(0, enc - aff);
}

   @Transactional
public LettrerManyResult lettrerMany(LettrerManyRequest req, String username) {

  double credit = creditDisponible(req.getClientId(), req.getDevise());
  double creditAvant = credit;

  // Factures à traiter (ordre client)
  Map<Long, Facture> map = factureRepository.findAllById(req.getFactureIds())
      .stream().collect(Collectors.toMap(Facture::getId, f -> f));

  // Encaissements du client (FIFO)
  List<TransactionCaisse> encaissements = transactionRepository
      .findByClient_IdAndDeviseAndTypeOrderByDateTransactionAsc(
          req.getClientId(), req.getDevise(), TypeTransaction.ENCAISSEMENT
      );

  // Cache affecté par tx (évite N requêtes)
  Map<Long, Double> dejaAffecteByTx = new HashMap<>();
  for (TransactionCaisse tx : encaissements) {
    double deja = facturePaiementRepository.sumAffecteByTransactionId(tx.getId());
    dejaAffecteByTx.put(tx.getId(), deja);
  }

  LettrerManyResult result = new LettrerManyResult();
  result.setCreditAvant(creditAvant);
  List<LettrerLine> lignes = new ArrayList<>();

  for (Long factureId : req.getFactureIds()) {

    Facture f = map.get(factureId);
    if (f == null) continue;

    // sécurité client + devise
    if (f.getClient() == null || !f.getClient().getId().equals(req.getClientId())) continue;
    if (f.getDevise() != req.getDevise()) continue;

    // PAID => skip
    if (f.getStatut() == StatutFacture.PAID) continue;

    double totalNet = f.getMontantTotal() - (f.getRemise() > 0 ? f.getRemise() : 0);
    double resteAvant = totalNet - f.getMontantPaye();

    if (resteAvant <= 0) {
      f.setStatut(StatutFacture.PAID);
      factureRepository.save(f);
      continue;
    }

    if (credit <= 0) break; // plus de crédit global

    double aPayer = Math.min(credit, resteAvant);
    double resteAffecter = aPayer;

    // ✅ affectation réelle sur les encaissements FIFO
    for (TransactionCaisse tx : encaissements) {
      if (resteAffecter <= 0) break;

      double deja = dejaAffecteByTx.getOrDefault(tx.getId(), 0.0);
      double dispo = tx.getMontant() - deja;
      if (dispo <= 0) continue;

      double affecte = Math.min(dispo, resteAffecter);

      FacturePaiement fp = new FacturePaiement();
      fp.setFacture(f);
      fp.setTransaction(tx);
      fp.setMontantAffecte(affecte);
      fp.setCreatedBy(username);
      facturePaiementRepository.save(fp);

      dejaAffecteByTx.put(tx.getId(), deja + affecte);
      resteAffecter -= affecte;
    }

    // maj facture
    StatutFacture avant = f.getStatut();
    f.setMontantPaye(f.getMontantPaye() + aPayer);

    double resteApres = totalNet - f.getMontantPaye();
    if (resteApres <= 0) f.setStatut(StatutFacture.PAID);
    else f.setStatut(StatutFacture.PARTIAL);

    factureRepository.save(f);

    // maj crédit global
    credit -= aPayer;

    LettrerLine line = new LettrerLine();
    line.setFactureId(f.getId());
    line.setRefFacture(f.getRefFacture());
    line.setStatutAvant(avant);
    line.setStatutApres(f.getStatut());
    line.setResteAvant(resteAvant);
    line.setAffecte(aPayer);
    line.setResteApres(Math.max(0, resteApres));
    lignes.add(line);
  }

  result.setCreditApres(credit);
  result.setLignes(lignes);
  return result;
}

  @Transactional
public LettrageContextDto getLettrageContext(Long clientId, Devise devise) {

    // 1️⃣ Vérifier client
    Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new RuntimeException("Client introuvable"));

    // 2️⃣ Crédit disponible
    double encaisse = transactionRepository
        .findByClient_IdAndDeviseAndTypeOrderByDateTransactionAsc(
            clientId,
            devise,
            TypeTransaction.ENCAISSEMENT
        )
        .stream()
        .mapToDouble(TransactionCaisse::getMontant)
        .sum();

    double affecte = facturePaiementRepository
        .findAll()
        .stream()
        .filter(fp -> fp.getFacture() != null)
        .filter(fp -> fp.getFacture().getClient() != null)
        .filter(fp -> fp.getFacture().getClient().getId().equals(clientId))
        .filter(fp -> fp.getFacture().getDevise() == devise)
        .mapToDouble(FacturePaiement::getMontantAffecte)
        .sum();

    double creditDisponible = Math.max(0, encaisse - affecte);

    // 3️⃣ Factures EMIS + PARTIAL
   List<Facture> factures = factureRepository
    .findByClient_IdAndDeviseAndStatutAndMotifAvoirIsNullOrderByDateEmissionAsc(
        clientId,
        devise,
        StatutFacture.EMIS
    );
    // 4️⃣ Construire mini DTO
    List<FactureMiniDto> mini = factures.stream().map(f -> {

        double remise = f.getRemise();
        double totalNet = f.getMontantTotal() - (remise > 0 ? remise : 0);
        double reste = totalNet - f.getMontantPaye();

        FactureMiniDto dto = new FactureMiniDto();
        dto.setId(f.getId());
        dto.setRefFacture(f.getRefFacture());
        dto.setDateEmission(f.getDateEmission());
        dto.setTotalNet(totalNet);
        dto.setMontantPaye(f.getMontantPaye());
        dto.setReste(Math.max(0, reste));
        dto.setStatut(f.getStatut());

        return dto;

    }).toList();

    // 5️⃣ Retour
    LettrageContextDto context = new LettrageContextDto();
    context.setClientId(clientId);
    context.setDevise(devise);
    context.setCreditDisponible(creditDisponible);
    context.setFactures(mini);

    return context;
}
}