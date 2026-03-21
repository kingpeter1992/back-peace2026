package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.AffectationRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.GardienPhotoRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PlainteRepository;
import com.king.peace.Dao.PointageRepository;
import com.king.peace.Dao.TauxJournalierRepository;
import com.king.peace.Dao.TransactionCaisseRepository;
import com.king.peace.Dto.GardienDetailsDto;
import com.king.peace.Dto.GardienDto;
import com.king.peace.Dto.GardienStatsDto;
import com.king.peace.Dto.PlainteDto;
import com.king.peace.Dto.PresenceDto;
import com.king.peace.Dto.ReponseDto;
import com.king.peace.Dto.TransactionCaisseDto;
import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.GardienPhoto;
import com.king.peace.Entitys.Plaintes;
import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.ReponsePlainte;
import com.king.peace.Entitys.StatutAffectation;
import com.king.peace.Entitys.StatutGardien;
import com.king.peace.Entitys.StatutPointage;
import com.king.peace.Entitys.TauxJournalier;
import com.king.peace.enums.Departement;
import com.king.peace.enums.Fonction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GardienService {
    private final GardienRepository gardienRepository;
    private final AffectationRepository affectationRepository;
    private final PointageRepository pointageRepository;
    private final TauxJournalierRepository tauxJournalierRepository;
    private final GardienPhotoRepository gardienPhotoRepository;
    private final PlainteRepository plainteRepository;
    private final TauxJournalierRepository tauxChangeRepository;
    private final TransactionCaisseRepository repoTrasanction;

    private static final Logger log = LoggerFactory.getLogger(GardienService.class);

    // ==============================
    // CRUD GARDIEN
    // ==============================
    public Gardien create(Gardien existing) {
        existing.setCreatedAt(LocalDate.now());
        existing.setNom(existing.getNom().toUpperCase());
        existing.setPrenom(existing.getPrenom().toUpperCase());
        existing.setTelephone1(existing.getTelephone1());
        existing.setTelephone2(existing.getTelephone2());
        existing.setAdresse(existing.getAdresse().toUpperCase());
        existing.setGenre(existing.getGenre().toUpperCase());
        existing.setSalaireBase(existing.getSalaireBase());
        existing.setStatut(existing.getStatut());
        existing.setDateEmbauche(existing.getDateEmbauche());
        existing.setNbrjours(existing.getNbrjours());

        if (existing.getStatut().equals(StatutGardien.ACTIF)) {
            existing.setActif(true);
        } else {
            existing.setActif(false);
        }

        return gardienRepository.save(existing);
    }

    public List<Gardien> findAll() {
        return gardienRepository.findAll();
    }

    public Gardien findById(Long id) {
        return gardienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gardien introuvable"));
    }

    public Gardien update(Long id, Gardien g) {
        Gardien existing = findById(id);
        existing.setNom(g.getNom().toUpperCase());
        existing.setPrenom(g.getPrenom().toUpperCase());
        existing.setTelephone1(g.getTelephone1());
        existing.setTelephone2(g.getTelephone2());
        existing.setAdresse(g.getAdresse().toUpperCase());
        existing.setGenre(g.getGenre().toUpperCase());
        existing.setSalaireBase(g.getSalaireBase());
        existing.setStatut(g.getStatut());
        existing.setDateEmbauche(g.getDateEmbauche());
        existing.setNbrjours(g.getNbrjours());
        return gardienRepository.save(existing);
    }

    public void delete(Long id) {
        gardienRepository.deleteById(id);
    }

    // ==============================
    // AFFECTATION AUX CONTRATS
    // ==============================
    public Affectation affecterGardien(Long gardienId, Contrats contrat) {
        Gardien g = findById(gardienId);

        // Vérifie qu'il n'y a pas déjà une affectation ACTIVE
        Optional<Affectation> dejaActif = affectationRepository
                .findByGardienIdAndStatut(gardienId, StatutAffectation.ACTIVE);
        if (dejaActif.isPresent()) {
            throw new RuntimeException("Ce gardien est déjà affecté à un contrat actif !");
        }

        Affectation a = Affectation.builder()
                .gardien(g)
          //      .contrat(contrat)
                .dateDebut(LocalDate.now())
                .statut(StatutAffectation.ACTIVE)
                .build();

        return affectationRepository.save(a);
    }

    // ==============================
    // POINTAGE
    // ==============================
    public Pointage pointerGardien(Long gardienId, LocalDate date, StatutPointage statut) {
        Gardien g = findById(gardienId);

        Pointage p = Pointage.builder()
                .gardien(g)
                .date(date)
                .statut(statut)
                .build();

        return pointageRepository.save(p);
    }

    public List<Pointage> getPointages(Long gardienId) {
        return pointageRepository.findByGardienId(gardienId);
    }

    // ==============================
    // TAUX JOURNALIER
    // ==============================
    public double getDernierTaux() {
        return tauxChangeRepository
                .findTopByOrderByDateDesc()
                .map(TauxJournalier::getTaux)
                .orElse(1.0); // sécurité
    }

    public TauxJournalier getTauxJournalier() {
        return tauxJournalierRepository.findTopByOrderByDateDesc()
                .orElseThrow(() -> new RuntimeException("Taux journalier non défini pour ce gardien"));
    }

    public void supprimerGardien(Long gardienId) {
        Gardien gardien = findById(gardienId);

        // Vérifier si le gardien a des affectations
        boolean aAffectation = affectationRepository.existsByGardienId(gardienId);

        // Vérifier si le gardien a des transactions (paiements, avances, etc.)

        if (aAffectation) {
            throw new RuntimeException(
                    "Impossible de supprimer ce gardien : il a des affectations ou des transactions. Vous pouvez le bloquer.");
        }

        gardienRepository.delete(gardien);
    }

    public Gardien bloquerGardien(Long gardienId) {
        Gardien gardien = findById(gardienId);
        gardien.setStatut(StatutGardien.BLOQUE); // StatutGardien peut être ACTIF / INACTIF / BLOQUE
        return gardienRepository.save(gardien);
    }

    public List<GardienDto> findAllWithPhoto() {

        List<Gardien> gardiens = gardienRepository.findAll();

        List<GardienDto> dtos = new ArrayList<>();
        for (Gardien g : gardiens) {

            // Récupère la première photo du gardien s’il existe
            GardienPhoto photo = gardienPhotoRepository.findFirstByGardienId(g.getId());

            GardienDto dto = new GardienDto();

            dto.setId(g.getId());
            dto.setNom(g.getNom());
            dto.setPrenom(g.getPrenom());
            dto.setTelephone1(g.getTelephone1());
            dto.setTelephone2(g.getTelephone2());
            dto.setFonction(g.getFonction());
            dto.setSalaire(g.getSalaire());
            dto.setSalaireBase(g.getSalaireBase());
            dto.setAdresse(g.getAdresse());
            dto.setGenre(g.getGenre());
            dto.setDateEmbauche(g.getDateEmbauche());
            dto.setEmail(g.getEmail());
            dto.setDateNaissance(g.getDateNaissance());
            dto.setCreatedAt(g.getCreatedAt());
            dto.setDevise(g.getDevise());
            dto.setStatut(g.getStatut());
            dto.setNbrjours(g.getNbrjours());
            dto.setDepartement(g.getDepartement());
            dto.setSite(g.getSite());
            dto.setFonction(g.getFonction());


            if (photo != null && photo.getPhoto() != null) {
                dto.setPhotoBase64(Base64.getEncoder().encodeToString(photo.getPhoto()));
            } else {
                dto.setPhotoBase64(null);
            }

            dtos.add(dto);
        }

        return dtos;
    }
    private final ContratRepository contratRepository;
    public GardienStatsDto getStats() {
        GardienStatsDto stats = new GardienStatsDto();
        stats.setTotalGardiens(gardienRepository.count());
        stats.setActifs(gardienRepository.countByStatut(StatutGardien.ACTIF));
        stats.setInactifs(gardienRepository.countByStatut(StatutGardien.INACTIF));
        double salaireCDF = gardienRepository.countSalairesCDF();
        double salaireUSD = gardienRepository.countSalairesUSD();
        stats.setSalaireCDF(salaireCDF);
        stats.setSalaireUSD(salaireUSD);

        Long countZone = contratRepository.countDistinctZones();
            stats.setSites(countZone);
        return stats;
    }

    public GardienDetailsDto getGardienDetails(Long id,
            LocalDate dateFrom,
            LocalDate dateTo) {

        try {

            log.info("🔵 START getGardienDetails id={}", id);

            LocalDateTime from = dateFrom.atStartOfDay();
            LocalDateTime to = dateTo.atTime(23, 59, 59);

            /*
             * =======================
             * GARDIEN
             * =======================
             */
            log.info("1️⃣ Chargement gardien...");
            Gardien g = gardienRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Gardien non trouvé"));
            /* ======================= INFOS GARDIEN ======================= */
            GardienDto dto = new GardienDto();
            dto.setId(g.getId());
            dto.setNom(g.getNom());
            dto.setPrenom(g.getPrenom());
            dto.setTelephone1(g.getTelephone1());
            dto.setTelephone2(g.getTelephone2());
            dto.setFonction(g.getFonction());
            dto.setSalaire(g.getSalaire());
            dto.setSalaireBase(g.getSalaireBase());
            dto.setAdresse(g.getAdresse());
            dto.setGenre(g.getGenre());
            dto.setDateEmbauche(g.getDateEmbauche());
            dto.setEmail(g.getEmail());
            dto.setDateNaissance(g.getDateNaissance());
            dto.setCreatedAt(g.getCreatedAt());
            dto.setDevise(g.getDevise());
            dto.setStatut(g.getStatut());
            dto.setNbrjours(g.getNbrjours());
            dto.setDepartement(g.getDepartement());

            log.info("✅ Gardien OK");

            /*
             * =======================
             * PHOTOS
             * =======================
             */
            log.info("2️⃣ Chargement photos...");
            /* ======================= PHOTO ======================= */
            List<GardienPhoto> photos = gardienPhotoRepository
                    .findAllByGardienId(g.getId());
            List<String> photoBase64 = photos.stream()
                    .map(p -> Base64.getEncoder()
                            .encodeToString(p.getPhoto()))
                    .toList();
            log.info("✅ Photos OK");

            /*
             * =======================
             * TRANSACTIONS
             * =======================
             */
            log.info("3️⃣ Chargement transactions...");
            List<TransactionCaisseDto> paiements = repoTrasanction
                    .findByGardienIdAndDateTransactionBetween(id, from, to)
                    .stream()
                    .map(h -> new TransactionCaisseDto(
                            h.getId(),
                            h.getDevise(),
                            h.getDateTransaction(),
                            h.getType(),
                            h.getCategory(),
                            h.getModePaiement(),
                            h.getMontant(),
                            h.getSens(),
                            h.getDescription(),
                            h.getReference(),
                            h.getSoldeAvant(),
                            h.getSoldeApres(),
                            h.getUserId()))
                    .toList();
            log.info("✅ Transactions OK");

            /*
             * =======================
             * PRESENCES
             * =======================
             */
            log.info("4️⃣ Chargement presences...");
            List<PresenceDto> presences = pointageRepository.findByGardienIdAndDateBetween(g.getId(), dateFrom, dateTo)
                    .stream()
                    .map(p -> PresenceDto.builder()
                            .id(p.getId())
                            .date(p.getDate())
                            .datesortie(p.getDatesortie())
                            .heureEntree(p.getHeureEntree())
                            .heureSortie(p.getHeureSortie())
                            .gardien(p.getGardien())
                            .statut(p.getStatut())
                            .build())
                    .toList();
            log.info("✅ Presences OK");

            /*
             * =======================
             * PLAINTES
             * =======================
             */
            log.info("5️⃣ Chargement plaintes...");
            List<PlainteDto> plaintes = plainteRepository
                    .findByGardien_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
                            g.getId(), dateFrom, dateFrom)
                    .stream()
                    .map(this::mapToDto)
                    .toList();
            log.info("✅ Plaintes OK");

            /*
             * =======================
             * STATS
             * =======================
             */
            log.info("6️⃣ Chargement stats...");
            GardienStatsDto stats = new GardienStatsDto();
            stats.setTotalGardiens(gardienRepository.count());
            stats.setActifs(gardienRepository.countByStatut(StatutGardien.ACTIF));
            stats.setInactifs(gardienRepository.countByStatut(StatutGardien.BLOQUE));
            stats.setSites(5);
            log.info("✅ Stats OK");

            log.info("🟢 FIN getGardienDetails OK");

            // Construire DTO final normalement
            /* ======================= DTO FINAL ======================= */
            GardienDetailsDto detailsDto = new GardienDetailsDto();
            detailsDto.setGardien(dto);
            detailsDto.setPaiements(paiements);
            detailsDto.setPresences(presences);
            detailsDto.setPlainte(plaintes);
            detailsDto.setPhotos(photoBase64);
            detailsDto.setStats(stats);
            // ...

            // ...
            return detailsDto;

        } catch (Exception e) {

            log.error("❌ ERREUR dans getGardienDetails id=" + id, e);

            throw e; // très important pour garder le vrai stacktrace
        }
    }

    public GardienStatsDto statsbyid(Long id) {
        GardienStatsDto stats = new GardienStatsDto();
        Gardien g = findById(id);
        stats.setTotalGardiens(gardienRepository.count());
        stats.setActifs(gardienRepository.countByStatut(StatutGardien.ACTIF));
        stats.setInactifs(gardienRepository.countByStatut(StatutGardien.BLOQUE));
        stats.setSites(5);
        return stats;
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

 public void importerDepuisExcel(List<GardienDto> dtos) {
        List<Gardien> gardiens = dtos.stream().map(this::mapToEntity).toList();
        gardienRepository.saveAll(gardiens);
    }


        private Gardien mapToEntity(GardienDto dto) {
        Gardien g = new Gardien();

        g.setNom(dto.getNom());
        g.setPrenom(dto.getPrenom());
        g.setTelephone1(dto.getTelephone1());
        g.setTelephone2(dto.getTelephone2());
        g.setSalaire(dto.getSalaire());
        g.setAdresse(dto.getAdresse());
        g.setGenre(dto.getGenre());
        g.setSalaireBase(dto.getSalaireBase());
        g.setStatut(dto.getStatut());
        g.setDateEmbauche(dto.getDateEmbauche());
        g.setEmail(dto.getEmail());
        g.setDateNaissance(dto.getDateNaissance());
        g.setCreatedAt(LocalDate.now());
        g.setDevise(dto.getDevise());
        g.setActif(true);
        g.setDepartement(dto.getDepartement());
        g.setFonction(dto.getFonction());
        g.setNbrjours(dto.getNbrjours());
        g.setSite(dto.getSite());

        return g;
    }
    
}
