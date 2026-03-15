package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.king.peace.Dao.AvanceSalaireRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PaieLigneRepository;
import com.king.peace.Dao.PaieRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dao.PrimeRepository;
import com.king.peace.Dto.PaieDTO;
import com.king.peace.Dto.PaieGenerationItemDTO;
import com.king.peace.Dto.PaieGenerationMasseDTO;
import com.king.peace.Dto.PaieGenerationMasseRequestDTO;
import com.king.peace.Dto.PaieSuppressionItemDTO;
import com.king.peace.Dto.PaiementDashboardDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Paie;
import com.king.peace.Entitys.PaieLigne;
import com.king.peace.Entitys.Pret;
import com.king.peace.Entitys.Prime;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.StatutAvance;
import com.king.peace.enums.StatutPaie;
import com.king.peace.enums.StatutPret;
import com.king.peace.enums.StatutPrime;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementSalaireServiceImpl  {


     private final GardienRepository  gardienRepository;
    private final PrimeRepository primeRepository;
    private final AvanceSalaireRepository avanceRepository;
    private final PretRepository pretRepository;
    private final PaieRepository paieRepository;


    public List<PaieDTO> findAll() {
        return paieRepository.findAllWithDetails()
                .stream()
                .map(PaieMapper::toDtoComplet)
                .toList();
    }
    public PaieDTO findById(Long id) {
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable"));
        return PaieMapper.toDtoComplet(paie);
    }

 public List<PaieDTO> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {

    if (dateDebut == null || dateFin == null) {
        throw new RuntimeException("La période est obligatoire");
    }

    if (dateFin.isBefore(dateDebut)) {
        throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
    }

    return paieRepository.findByDatePaieFinBetweenWithDetails(dateDebut, dateFin)
            .stream()
            .map(PaieMapper::toDtoComplet)
            .toList();
}


    public List<PaieDTO> findByGardien(Long gardienId) {
        return paieRepository.findByGardienIdWithDetails(gardienId)
                .stream()
                .map(PaieMapper::toDtoComplet)
                .toList();
    }
    public PaieDTO payer(Long id) {
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable"));

        paie.setStatut(StatutPaie.PAYE);
        return PaieMapper.toDtoComplet(paie);
    }

    public PaieDTO annuler(Long id) {
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable"));

        paie.setStatut(StatutPaie.ANNULE);
        return PaieMapper.toDtoComplet(paie);
    }

    public void delete(Long id) {
        paieRepository.deleteById(id);
    }


@Transactional
public PaiementDashboardDTO statistique(LocalDate dateDebut, LocalDate dateFin) {

    List<Gardien> gardiens = gardienRepository.findByActifTrue();

    long totalEmployes = gardiens.size();

    double salaireBaseUsd = 0;
    double salaireBaseCdf = 0;

    double totalPrimesUsd = 0;
    double totalPrimesCdf = 0;

    double totalAvancesUsd = 0;
    double totalAvancesCdf = 0;

    double totalPretsUsd = 0;
    double totalPretsCdf = 0;

    double totalRetenuesUsd = 0;
    double totalRetenuesCdf = 0;

    double netAPayerUsd = 0;
    double netAPayerCdf = 0;

    for (Gardien g : gardiens) {

        double salaireBase = value(g.getSalaireBase());
        Devise devise = g.getDevise();

        if (devise == null) continue;

        // ===== PRIMES VALIDÉES =====
        double primes = primeRepository.sumPrimesPourPaie(
                g.getId(),
                dateDebut,
                dateFin,
                StatutPrime.VALIDEE,
                devise
        );

        // ===== AVANCES VALIDÉES =====
        double avances = avanceRepository.sumAvancesPourPaie(
                g.getId(),
                dateDebut,
                dateFin,
                StatutAvance.VALIDEE,
                devise
        );

        // ===== PRÊTS EN COURS =====
        double prets = pretRepository.sumMensualitePretsPourPaie(
                g.getId(),
                StatutPret.EN_COURS,
                devise
        );

        double retenues = avances + prets;

        double net = salaireBase + primes - retenues;

        if (devise == Devise.USD) {

            salaireBaseUsd += salaireBase;
            totalPrimesUsd += primes;
            totalAvancesUsd += avances;
            totalPretsUsd += prets;
            totalRetenuesUsd += retenues;
            netAPayerUsd += net;

        } else {

            salaireBaseCdf += salaireBase;
            totalPrimesCdf += primes;
            totalAvancesCdf += avances;
            totalPretsCdf += prets;
            totalRetenuesCdf += retenues;
            netAPayerCdf += net;
        }
    }

    return PaiementDashboardDTO.builder()
            .totalEmployes(totalEmployes)

            .salaireBaseUsd(salaireBaseUsd)
            .salaireBaseCdf(salaireBaseCdf)

            .totalPrimesUsd(totalPrimesUsd)
            .totalPrimesCdf(totalPrimesCdf)

            .totalAvancesUsd(totalAvancesUsd)
            .totalAvancesCdf(totalAvancesCdf)

            .totalPretsUsd(totalPretsUsd)
            .totalPretsCdf(totalPretsCdf)

            .totalRetenuesUsd(totalRetenuesUsd)
            .totalRetenuesCdf(totalRetenuesCdf)

            .netAPayerUsd(netAPayerUsd)
            .netAPayerCdf(netAPayerCdf)

            .build();
}

private double value(Double v) {
    return v != null ? v : 0;
}

  private final PaieUnitService paieUnitService;

    public PaieDTO genererPaie(Long gardienId, LocalDate dateDebut, LocalDate dateFin) {
        return paieUnitService.genererPaieUnitaire(gardienId, dateDebut, dateFin);
    }

public PaieGenerationMasseDTO genererPaieMasse(PaieGenerationMasseRequestDTO request) {

        if (request == null || request.getGardienIds() == null || request.getGardienIds().isEmpty()) {
            throw new RuntimeException("Aucun gardien sélectionné");
        }

        if (request.getDateDebut() == null || request.getDateFin() == null) {
            throw new RuntimeException("La période est obligatoire");
        }

        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
        }

        List<Gardien> gardiens = gardienRepository.findAllById(request.getGardienIds());
        List<PaieGenerationItemDTO> details = new ArrayList<>();

        int succes = 0;
        int echecs = 0;

        for (Gardien gardien : gardiens) {
            try {
                if (!gardien.isActif()) {
                    throw new RuntimeException("Gardien inactif");
                }

                PaieDTO paie = paieUnitService.genererPaieUnitaire(
                        gardien.getId(),
                        request.getDateDebut(),
                        request.getDateFin()
                );

                details.add(PaieGenerationItemDTO.builder()
                        .gardienId(gardien.getId())
                        .gardienNom(gardien.getNom() + " " + gardien.getPrenom())
                        .succes(true)
                        .message("Paie générée avec succès")
                        .paie(paie)
                        .build());
                succes++;

            } catch (Exception e) {
                details.add(PaieGenerationItemDTO.builder()
                        .gardienId(gardien.getId())
                        .gardienNom(gardien.getNom() + " " + gardien.getPrenom())
                        .succes(false)
                        .message(e.getMessage())
                        .paie(null)
                        .build());
                echecs++;
            }
        }

        return PaieGenerationMasseDTO.builder()
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .totalGardiens(gardiens.size())
                .totalSucces(succes)
                .totalEchecs(echecs)
                .details(details)
                .build();
    }




@Transactional
public List<PaieDTO> annulerMasse(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        throw new RuntimeException("Aucune paie sélectionnée");
    }

    List<PaieDTO> resultats = new ArrayList<>();

    for (Long id : ids) {
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

        StatutPaie statut = paie.getStatut();

        if (statut != StatutPaie.BROUILLON) {
            throw new RuntimeException(
                "Impossible d'annuler la paie " + id + " : seul le statut BROUILLON est annulable"
            );
        }

        paie.setStatut(StatutPaie.ANNULE);
        resultats.add(PaieMapper.toDtoComplet(paie));
    }

    return resultats;
}

@Transactional
public List<PaieDTO> validerMasse(List<Long> ids) {

    if (ids == null || ids.isEmpty()) {
        throw new RuntimeException("Aucune paie sélectionnée");
    }

    List<PaieDTO> resultats = new ArrayList<>();

    for (Long id : ids) {
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

        if (paie.getStatut() != StatutPaie.BROUILLON) {
            throw new RuntimeException("Seule une paie BROUILLON peut être validée : " + id);
        }

        if (paie.getPaieLignes() != null) {
            for (PaieLigne ligne : paie.getPaieLignes()) {

                if (ligne.getReferenceId() == null) continue;

                switch (ligne.getTypeLigne()) {

                    case PRIME -> {
                        Prime prime = primeRepository.findById(ligne.getReferenceId())
                                .orElseThrow(() -> new RuntimeException("Prime introuvable : " + ligne.getReferenceId()));

                        if (prime.getStatut() == StatutPrime.VALIDEE) {
                            prime.setStatut(StatutPrime.PAYEE);
                            primeRepository.save(prime);
                        }
                    }

                    case AVANCE -> {
                        AvanceSalaire avance = avanceRepository.findById(ligne.getReferenceId())
                                .orElseThrow(() -> new RuntimeException("Avance introuvable : " + ligne.getReferenceId()));

                        if (avance.getStatut() == StatutAvance.VALIDEE) {
                            avance.setStatut(StatutAvance.DEDUITE);
                            avanceRepository.save(avance);
                        }
                    }

                    case PRET -> {
                        Pret pret = pretRepository.findById(ligne.getReferenceId())
                                .orElseThrow(() -> new RuntimeException("Prêt introuvable : " + ligne.getReferenceId()));

                        double restant = (pret.getMontantRestant() != 0 ? pret.getMontantRestant() : 0.0) - ligne.getMontant();

                        if (restant <= 0) {
                            pret.setMontantRestant(0.0);
                            pret.setStatut(StatutPret.TERMINE);
                        } else {
                            pret.setMontantRestant(restant);
                            pret.setStatut(StatutPret.EN_COURS);
                        }

                        pretRepository.save(pret);
                    }

                    default -> {
                    }
                }
            }
        }

        paie.setStatut(StatutPaie.VALIDE);
        resultats.add(PaieMapper.toDtoComplet(paie));
    }

    return resultats;
}

@Transactional
public List<PaieDTO> payerMasse(List<Long> ids) {

    if (ids == null || ids.isEmpty()) {
        throw new RuntimeException("Aucune paie sélectionnée");
    }

    List<PaieDTO> resultats = new ArrayList<>();

    for (Long id : ids) {
        Paie paie = paieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

        if (paie.getStatut() != StatutPaie.VALIDE) {
            throw new RuntimeException("Seule une paie VALIDEE peut être payée : " + id);
        }

        paie.setStatut(StatutPaie.PAYE);
        resultats.add(PaieMapper.toDtoComplet(paie));
    }

    return resultats;
}

@Transactional
public void supprimerPaie(Long id) {

    Paie paie = paieRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

    if (paie.getStatut() == StatutPaie.VALIDE || paie.getStatut() == StatutPaie.PAYE) {
        rollbackImpactsPaie(paie);
    }

    // supprimer les lignes d'abord si nécessaire
    if (paie.getPaieLignes() != null && !paie.getPaieLignes().isEmpty()) {
        paieLigneRepository.deleteAll(paie.getPaieLignes());
    }

    paieRepository.delete(paie);
}
    private final PaieLigneRepository paieLigneRepository;

@Transactional
public List<PaieSuppressionItemDTO> supprimerMasse(List<Long> ids) {

    if (ids == null || ids.isEmpty()) {
        throw new RuntimeException("Aucune paie sélectionnée");
    }

    List<PaieSuppressionItemDTO> resultats = new ArrayList<>();

    for (Long id : ids) {
        try {
            Paie paie = paieRepository.findByIdWithDetails(id)
                    .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

            if (paie.getStatut() != StatutPaie.BROUILLON && paie.getStatut() != StatutPaie.ANNULE) {
                throw new RuntimeException(
                        "Seules les paies BROUILLON ou ANNULE peuvent être supprimées"
                );
            }

            if (paie.getPaieLignes() != null && !paie.getPaieLignes().isEmpty()) {
                paieLigneRepository.deleteAll(paie.getPaieLignes());
            }

            paieRepository.delete(paie);

            resultats.add(PaieSuppressionItemDTO.builder()
                    .paieId(id)
                    .succes(true)
                    .message("Paie supprimée avec succès")
                    .build());

        } catch (Exception e) {
            resultats.add(PaieSuppressionItemDTO.builder()
                    .paieId(id)
                    .succes(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    return resultats;
}

private void rollbackImpactsPaie(Paie paie) {

    if (paie.getPaieLignes() == null || paie.getPaieLignes().isEmpty()) {
        return;
    }

    for (PaieLigne ligne : paie.getPaieLignes()) {

        if (ligne.getReferenceId() == null) {
            continue;
        }

        switch (ligne.getTypeLigne()) {

            case PRIME -> {
                Prime prime = primeRepository.findById(ligne.getReferenceId())
                        .orElseThrow(() -> new RuntimeException("Prime introuvable : " + ligne.getReferenceId()));

                if (prime.getStatut() == StatutPrime.PAYEE) {
                    prime.setStatut(StatutPrime.VALIDEE);
                    primeRepository.save(prime);
                }
            }

            case AVANCE -> {
                AvanceSalaire avance = avanceRepository.findById(ligne.getReferenceId())
                        .orElseThrow(() -> new RuntimeException("Avance introuvable : " + ligne.getReferenceId()));

                if (avance.getStatut() == StatutAvance.DEDUITE) {
                    avance.setStatut(StatutAvance.VALIDEE);
                    avanceRepository.save(avance);
                }
            }

            case PRET -> {
                Pret pret = pretRepository.findById(ligne.getReferenceId())
                        .orElseThrow(() -> new RuntimeException("Prêt introuvable : " + ligne.getReferenceId()));

                double montantActuel = pret.getMontantRestant() != 0 ? pret.getMontantRestant() : 0.0;
                double montantARestaurer = ligne.getMontant() != 0 ? ligne.getMontant() : 0.0;

                pret.setMontantRestant(montantActuel + montantARestaurer);
                pret.setStatut(StatutPret.EN_COURS);

                pretRepository.save(pret);
            }

            default -> {
            }
        }
    }
}
}
