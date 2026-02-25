package com.king.peace.ImplementServices;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.FacturePaiementRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dao.PlainteRepository;
import com.king.peace.Dao.TransactionCaisseRepository;
import com.king.peace.Dto.ClientDetailsDto;
import com.king.peace.Dto.ClientDto;
import com.king.peace.Dto.ClientStatsDto;
import com.king.peace.Dto.ContratsdTO;
import com.king.peace.Dto.PlainteDto;
import com.king.peace.Dto.ReponseDto;
import com.king.peace.Dto.Response.ContratDetailsMiniDto;
import com.king.peace.Dto.Response.FactureMiniDto;
import com.king.peace.Dto.Response.PlainteMiniDto;
import com.king.peace.Dto.Response.TransactionMiniDto;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.Plaintes;
import com.king.peace.Entitys.ReponsePlainte;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.Entitys.StatutGardien;
import com.king.peace.Entitys.TransactionCaisse;
import com.king.peace.Entitys.TypeTransaction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ContratRepository contratRepository;
    private final FactureRepository factureRepository;
    private final TransactionCaisseRepository transactionRepository;
    private final PlainteRepository pl;

    // Création d'un client
    public Client creerClient(Client client) {
        client.setId(generateCode5());
        client.setNom(client.getNom().toUpperCase());
        client.setAdresse(client.getAdresse().toUpperCase());
        client.setContact(client.getContact().toUpperCase());
        client.setContact2(client.getContact2().toUpperCase());
        client.setEmail(client.getEmail().toUpperCase());
        client.setActif(true);

        return clientRepository.save(client);
    }

    private Long generateCode5() {
        int number = ThreadLocalRandom.current().nextInt(10000, 100000);
        return Long.valueOf(number);
    }

    // Signer un contrat pour un client
    public Contrats signerContrat(Long clientId, Contrats contrat) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        contrat.setClient(client);
        contrat.setStatut("ACTIF");
        return contratRepository.save(contrat);
    }

    // Historique des factures d'un client
    public List<Facture> getFacturesClient(Long clientId) {
        return factureRepository.findByClientId(clientId);
    }

    // Mettre à jour un client
    public ClientDto updateClient(Long id, Client client) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        existing.setNom(client.getNom().toUpperCase());
        existing.setAdresse(client.getAdresse().toUpperCase());
        existing.setContact(client.getContact().toUpperCase());
        existing.setContact2(client.getContact2().toUpperCase());
        existing.setEmail(client.getEmail().toUpperCase());
        existing.setTypeClient(client.getTypeClient().toUpperCase());

        Client saved = clientRepository.save(existing);

        // Retourne le DTO pour éviter le lazy loading
        return new ClientDto(
                saved.getId(),
                saved.getNom(),
                saved.getAdresse(),
                saved.getContact(),
                saved.getContact2(),
                saved.getEmail(),
                saved.getTypeClient(),
                saved.isActif());
    }

    // Supprimer un client
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        clientRepository.delete(client);
    }

    // Liste de tous les clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Obtenir un client par id
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
    }

    // Obtenir tous les contrats d'un client
    public List<Contrats> getContratsByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return contratRepository.findByClient(client);
    }

    // Obtenir toutes les factures d'un client
    public List<Facture> getFacturesByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return factureRepository.findByClient(client);
    }

    public Optional<Facture> findById(Long clientId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public ClientDetailsDto getClientDetails(Long clientId,
            LocalDate dateFrom,
            LocalDate dateTo

    ) {
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(23, 59, 59);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        // Convertir Client → ClientDto
        ClientDto clientDto = mapToClientDto(client);

        // Récupérer les factures du client
        List<FactureMiniDto> factures = factureRepository
                .findByClientId(clientId)
                .stream()
                .map(this::mapToFactureMiniDto)
                .toList();

        // le statitique
        ClientStatsDto getClientStats = getClientStats(clientId, dateFrom, dateTo);

        // LISTE CONTRAT
        List<ContratDetailsMiniDto> contrats = contratRepository
                .findByClientIdAndCreatedAtBetween(clientId, dateFrom, dateTo)
                .stream()
                .map(this::mapToClientToContrats)
                .toList();

        // trasactions

        List<TransactionMiniDto> tx = transactionRepository
                .findByClientIdAndDateTransactionBetween(clientId, from, to)
                .stream()
                .map(this::mapToClientToTrasaction)
                .toList();

        // plaintes
        List<PlainteDto> plaintes = pl
                .findByClient_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        clientId, dateFrom, dateTo)
                .stream()
                .map(this::mapToDto)
                .toList();

        ClientDetailsDto result = new ClientDetailsDto();
        result.setClient(clientDto);
        result.setFactures(factures);
        result.setStats(getClientStats);
        result.setContrats(contrats);
        result.setTransactions(tx);
        result.setPlainte(plaintes);

        return result;

    }

    private PlainteDto mapToDto(Plaintes p) {

        PlainteDto dto = new PlainteDto();

        dto.setId(p.getId());
        dto.setDescription(p.getDescription());
        dto.setDatePlainte(p.getDatePlainte());
        dto.setStatut(p.getStatut());
        dto.setNote(p.getNote());
        dto.setNiveau(p.getNiveau());
        dto.setDateLimiteReponse(p.getDateLimiteReponse());
        dto.setReponseGardien(p.getReponseGardien());
        dto.setRepondu(p.isRepondu());

        dto.setGardienId(p.getGardien().getId());
        dto.setClientId(p.getClient().getId());

        dto.setGardienNom(p.getGardien().getNom());
        dto.setClientNom(p.getClient().getNom());

        // ✅ Liste des réponses
        List<ReponseDto> reps = (p.getReponses() == null) ? List.of()
                : p.getReponses().stream()
                        .sorted(Comparator.comparing(ReponsePlainte::getDateReponse)) // optionnel
                        .map(this::mapToReponseDto)
                        .toList();

        dto.setListeReponses(reps);

        return dto;
    }

    private ReponseDto mapToReponseDto(ReponsePlainte r) {
        ReponseDto dto = new ReponseDto();
        dto.setId(r.getId());
        dto.setReponse(r.getReponse());
        dto.setDateReponse(r.getDateReponse());
        return dto;
    }
    // ========================
    // MAPPING METHODS
    // ========================

    private ClientDto mapToClientDto(Client c) {
        ClientDto dto = new ClientDto();
        dto.setId(c.getId());
        dto.setNom(c.getNom().toUpperCase());
        dto.setContact(c.getContact());
        dto.setContact2(c.getContact2());
        dto.setEmail(c.getEmail());
        dto.setAdresse(c.getAdresse().toUpperCase());
        dto.setTypeClient(c.getTypeClient());
        return dto;
    }

    private TransactionMiniDto mapToClientToTrasaction(TransactionCaisse t) {
        TransactionMiniDto dto = new TransactionMiniDto();
        dto.setId(t.getId());
        dto.setMontant(t.getMontant());
        dto.setType(t.getType());
        dto.setCategory(t.getCategory());
        
        dto.setDevise(t.getDevise());
        dto.setReference(t.getReference());
        dto.setSoldeAvant(t.getSoldeAvant());
        dto.setSoldeApres(t.getSoldeApres());
        dto.setSens(t.getSens());
        dto.setUserId(t.getUserId());
        dto.setDateTransaction(t.getDateTransaction());
        dto.setCategory(t.getCategory());
        return dto;

    }

    private FactureMiniDto mapToFactureMiniDto(Facture f) {
        FactureMiniDto dto = new FactureMiniDto();
        dto.setId(f.getId());
        dto.setReference(f.getRefFacture());
        dto.setClientId(f.getId());
        dto.setCommentaire(f.getCommentaire());
        dto.setDateEmission(f.getDateEmission());
        dto.setDescription(f.getDescription());
        dto.setDevise(f.getDevise());
        dto.setMontantParGardien(f.getMontantParGardien());
        dto.setMotifAvoir(f.getMotifAvoir());
        dto.setNombreGardiens(f.getNombreGardiens());
        dto.setNombreJours(f.getNombreJours());
        dto.setRemise(f.getRemise());
        dto.setStatut(f.getStatut());
        dto.setMontantPaye(f.getMontantPaye());
        dto.setReste(f.getReste());
        dto.setMontantTotal(f.getMontantTotal());

        return dto;
    }

    private PlainteMiniDto mapToClientToPlaintes(Plaintes p) {
        PlainteMiniDto dto = new PlainteMiniDto();
        dto.setId(p.getId());
        dto.setDatePlainte(p.getDatePlainte());
        dto.setNote(p.getNote());
        dto.setNiveau(p.getNiveau());
        return dto;

    }

    private ContratDetailsMiniDto mapToClientToContrats(Contrats c) {
        ContratDetailsMiniDto dto = new ContratDetailsMiniDto();
        dto.setId(c.getId());
        dto.setMontant(c.getMontant());
        dto.setStatut(c.getStatut());
        dto.setClient(dto.getClient());
        dto.setGardiens(dto.getGardiens());
        dto.setRefContrats(c.getRefContrats());
        dto.setDescription(c.getDescription());
        dto.setDateDebut(c.getDateDebut());
        dto.setDateFin(c.getDateFin());
        dto.setActive(c.isActive());
        dto.setMontantParGardien(c.getMontantParGardien());
        dto.setNombreGardiens(c.getNombreGardiens());
        dto.setNombreJoursMensuel(c.getNombreJoursMensuel());
        dto.setDevise(c.getDevise());
        dto.setActiviteClient(c.getActiviteClient());
        dto.setTypePaiement(c.getTypePaiement());
        dto.setDateDebutFacturation(c.getDateDebutFacturation());
        dto.setDateEmission(c.getDateEmission());
        dto.setZone(c.getZone());

        return dto;

    }

    public ClientStatsDto getClientStats(
            Long clientId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        ClientStatsDto stats = new ClientStatsDto();

        // =========================
        // CONTRATS
        // =========================
        stats.setTotalContrats(
                contratRepository.countByClientIdAndCreatedAtBetween(
                        clientId, dateFrom, dateTo));

        stats.setContratsActifs(
                contratRepository.countByClientIdAndActiveAndCreatedAtBetween(
                        clientId, true, dateFrom, dateTo));

        // =========================
        // FACTURES
        // =========================
        List<Facture> factures = factureRepository.findByClientIdAndDateEmissionBetween(
                clientId, dateFrom, dateTo);

        stats.setTotalFactures(factures.size());

     stats.setFacturesEmises(
    factures.stream()
        .filter(f -> f.getStatut() == StatutFacture.EMIS)
        .filter(f -> f.getMotifAvoir() == null || f.getMotifAvoir().isBlank())
        .count()
);

        stats.setFacturesPartial(
                factures.stream().filter(f -> f.getStatut() == StatutFacture.PARTIAL).count());

        stats.setFacturesPaid(
                factures.stream().filter(f -> f.getStatut() == StatutFacture.PAID).count());

        double totalFactureCDF = 0;
        double totalFactureUSD = 0;
        double totalPayeCDF = 0;
        double totalPayeUSD = 0;

for (Facture f : factures) {

    // ✅ Filtrer par statut autorisé
    if (f.getStatut() != StatutFacture.PAID
        && f.getStatut() != StatutFacture.EMIS
        && f.getStatut() != StatutFacture.PARTIAL
        && f.getStatut() != StatutFacture.ANNULE) {
        continue; // ⛔ on ignore la facture
    }

    double total = f.getMontantTotal() != 0 ? f.getMontantTotal() : 0.0;
    double paye  = f.getMontantPaye()  != 0 ? f.getMontantPaye()  : 0.0;

    if (f.getDevise() == Devise.CDF) {
        totalFactureCDF += total;
        totalPayeCDF    += paye;
    } else if (f.getDevise() == Devise.USD) {
        totalFactureUSD += total;
        totalPayeUSD    += paye;
    }
}

        stats.setTotalFactureCDF(totalFactureCDF);
        stats.setTotalFactureUSD(totalFactureUSD);
        stats.setTotalPayeCDF(totalPayeCDF);
        stats.setTotalPayeUSD(totalPayeUSD);

        stats.setImpayeCDF(totalFactureCDF - totalPayeCDF);
        stats.setImpayeUSD(totalFactureUSD - totalPayeUSD);

        // =========================
        // TRANSACTIONS (ENCAISSEMENTS)
        // =========================
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(23, 59, 59);
        List<TransactionCaisse> encaissements = transactionRepository
                .findByClientIdAndTypeAndDateTransactionBetween(
                        clientId,
                        TypeTransaction.ENCAISSEMENT,
                        from,
                        to);

        stats.setTotalTransactions(encaissements.size());

       double encCDF = 0.0;
double encUSD = 0.0;

for (TransactionCaisse t : encaissements) {

    double montant = t.getMontant() != 0 ? t.getMontant() : 0.0;

    if (t.getDevise() == Devise.CDF) {
        encCDF += montant;
    } else if (t.getDevise() == Devise.USD) {
        encUSD += montant;
    }
}

// ✅ Soustraction du montant déjà payé
encCDF -= totalPayeCDF;
encUSD -= totalPayeUSD;

// ✅ Sécurité : éviter valeurs négatives si nécessaire
encCDF = Math.max(0, encCDF);
encUSD = Math.max(0, encUSD);

stats.setEncaissementsCDF(encCDF);
stats.setEncaissementsUSD(encUSD);

return stats;
    }

}
