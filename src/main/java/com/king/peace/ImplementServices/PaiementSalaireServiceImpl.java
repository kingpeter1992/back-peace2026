package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.AvanceSalaireRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PaieLigneRepository;
import com.king.peace.Dao.PaieRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dao.PrimeRepository;
import com.king.peace.Dto.PaieDTO;
import com.king.peace.Dto.PaieGenerationForceDTO;
import com.king.peace.Dto.PaieGenerationItemDTO;
import com.king.peace.Dto.PaieGenerationMasseDTO;
import com.king.peace.Dto.PaieGenerationMasseRequestDTO;
import com.king.peace.Dto.PaiementDashboardDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Paie;
import com.king.peace.Entitys.PaieLigne;
import com.king.peace.Entitys.Pret;
import com.king.peace.Entitys.Prime;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.SensLignePaie;
import com.king.peace.enums.StatutAvance;
import com.king.peace.enums.StatutPaie;
import com.king.peace.enums.StatutPret;
import com.king.peace.enums.StatutPrime;
import com.king.peace.enums.TypeLignePaie;

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
    private final PaieLigneRepository paieLigneRepository;



    @Transactional
public PaieDTO genererPaie(Long gardienId, Integer mois, Integer annee) {


//1. Charger le gardien actif
    Gardien gardien = gardienRepository.findById(gardienId)
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));


            // 2. Vérifier si la paie de ce mois existe déjà pour ce gardien
    if (paieRepository.existsByGardienIdAndMoisAndAnnee(gardienId, mois, annee)) {
        throw new RuntimeException("La paie de ce mois existe déjà pour ce gardien");
    }

    if (!gardien.isActif()) {
    throw new RuntimeException("Gardien inactif");
}

if (paieRepository.existsByGardienIdAndMoisAndAnnee(gardienId, mois, annee)) {
    throw new RuntimeException("Paie déjà générée pour cette période");
}

if (gardien.getDevise() == null) {
    throw new RuntimeException("Devise de salaire absente");
}

if (gardien.getSalaireBase() == 0 || gardien.getSalaireBase() <= 0) {
    throw new RuntimeException("Salaire de base invalide");
}
    //récupérer salaire de base

    //récupérer devise du salaire de base
    Double salaireBase = gardien.getSalaireBase();
    Devise devise = gardien.getDevise();


//3. Charger les primes validées
    List<Prime> primes = primeRepository.findPrimesPourPaie(gardienId, mois, annee, StatutPrime.VALIDEE, devise);
  
    //4. Charger les avances validées
    List<AvanceSalaire> avances = avanceRepository.findAvancesPourPaie(gardienId, mois, annee, StatutAvance.VALIDEE, devise);
    
    //5. Charger les prêts en cours
    List<Pret> prets = pretRepository.findPretsEnCoursPourPaie(gardienId, StatutPret.EN_COURS, devise);

    //6. Calculer
    double totalPrimes = primes.stream().mapToDouble(p -> p.getMontant() != 0 ? p.getMontant() : 0).sum();
    double totalAvances = avances.stream().mapToDouble(a -> a.getMontant() != 0 ? a.getMontant() : 0).sum();
    double totalPrets = prets.stream().mapToDouble(p -> p.getMensualite() != 0 ? p.getMensualite() : 0).sum();

    double net = salaireBase + totalPrimes - totalAvances - totalPrets;


    //Statut initial
                Paie paie = new Paie();
                paie.setGardien(gardien);
                paie.setMois(mois);
                paie.setAnnee(annee);
                paie.setDatePaie(LocalDate.now());
                paie.setSalaireBase(salaireBase);
                paie.setDevise(devise);
                paie.setTotalPrimes(totalPrimes);
                paie.setTotalAvances(totalAvances);
                paie.setTotalPrets(totalPrets);
                paie.setAutresRetenues(0.0);
                paie.setNetAPayer(net);
                paie.setStatut(StatutPaie.BROUILLON);
                paie.setNumeroBulletin(generateNumeroBulletin(gardien.getId(), mois, annee));

                

    Paie savedPaie = paieRepository.save(paie);

    // Ligne salaire
    paieLigneRepository.save(PaieLigne.builder()
            .paie(savedPaie)
            .typeLigne(TypeLignePaie.SALAIRE)
            .referenceId(null)
            .libelle("Salaire de base")
            .montant(salaireBase)
            .sens(SensLignePaie.GAIN)
            .build());

    // Lignes primes
    for (Prime p : primes) {
        paieLigneRepository.save(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.PRIME)
                .referenceId(p.getId())
                .libelle("Prime - " + p.getTypePrime())
                .montant(p.getMontant())
                .sens(SensLignePaie.GAIN)
                .build());

        p.setStatut(StatutPrime.PAYEE);
    }

    // Lignes avances
    for (AvanceSalaire a : avances) {
        paieLigneRepository.save(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.AVANCE)
                .referenceId(a.getId())
                .libelle("Avance sur salaire")
                .montant(a.getMontant())
                .sens(SensLignePaie.RETENUE)
                .build());

        a.setStatut(StatutAvance.DEDUITE);
    }

    // Lignes prêts
    for (Pret pret : prets) {
        double mensualite = pret.getMensualite() != 0 ? pret.getMensualite() : 0.0;

        paieLigneRepository.save(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.PRET)
                .referenceId(pret.getId())
                .libelle("Mensualité prêt")
                .montant(mensualite)
                .sens(SensLignePaie.RETENUE)
                .build());

        double restant = (pret.getMontantRestant() != 0 ? pret.getMontantRestant() : 0.0) - mensualite;
        if (restant <= 0) {
            pret.setMontantRestant(0.0);
            pret.setStatut(StatutPret.TERMINE);
        } else {
            pret.setMontantRestant(restant);
        }
    }

    return PaieMapper.toDtoComplet(savedPaie);
}


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

    public List<PaieDTO> findByPeriode(Integer mois, Integer annee) {
        return paieRepository.findByMoisAndAnneeWithDetails(mois, annee)
                .stream()
                .map(PaieMapper::toDtoComplet)
                .toList();
    }


    public String generateNumeroBulletin(Long id, Integer mois, Integer annee) {
    LocalTime now = LocalTime.now();
    String time = String.format("%02d%02d%02d", now.getHour(), now.getMinute(), now.getSecond());
    String ref = String.format("%04d", id);
    return "PAIE-" + annee + String.format("%02d", mois) + "-" + time + "-" + ref;
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


@Transactional
public PaieGenerationMasseDTO genererPaieMasse(PaieGenerationMasseRequestDTO request) {

    List<Gardien> gardiens = gardienRepository.findByActifTrue();
    List<PaieGenerationItemDTO> details = new ArrayList<>();

    int succes = 0;
    int echecs = 0;

    for (Gardien gardien : gardiens) {
        try {
            PaieDTO paie = genererPaie(gardien.getId(), request.getMois(), request.getAnnee());

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
            .mois(request.getMois())
            .annee(request.getAnnee())
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

                if (paie.getStatut() == StatutPaie.ANNULE) {
    throw new RuntimeException("Cette paie est déjà annulée : " + id);
}

        // ❌ on ne peut annuler QUE si BROUILLON
        if (paie.getStatut() != StatutPaie.BROUILLON) {
            throw new RuntimeException(
                    "Impossible d'annuler la paie " + id +
                    " : seul le statut BROUILLON est annulable"
            );
        }

        paie.setStatut(StatutPaie.ANNULE);

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
        Paie paie = paieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Paie introuvable : " + id));

        if (paie.getStatut() == StatutPaie.ANNULE) {
            throw new RuntimeException("Impossible de payer une paie annulée : " + id);
        }

         if (paie.getStatut() != StatutPaie.VALIDE) {
            throw new RuntimeException("Seules les paies VALIDES peuvent être payées : " + id);
        }

        if (paie.getStatut() == StatutPaie.PAYE) {
            resultats.add(PaieMapper.toDtoComplet(paie));
            continue;
        }

       

        Long gardienId = paie.getGardien().getId();
        Integer mois = paie.getMois();
        Integer annee = paie.getAnnee();
        Devise devise = paie.getDevise();

        List<Prime> primes = primeRepository.findPrimesPourPaie(
                gardienId, mois, annee, StatutPrime.VALIDEE, devise
        );

        List<AvanceSalaire> avances = avanceRepository.findAvancesPourPaie(
                gardienId, mois, annee, StatutAvance.PAYEE, devise
        );

        List<Pret> prets = pretRepository.findPretsEnCoursPourPaie(
                gardienId, StatutPret.EN_COURS, devise
        );

        for (Prime p : primes) {
            p.setStatut(StatutPrime.PAYEE);
        }

        for (AvanceSalaire a : avances) {
            a.setStatut(StatutAvance.DEDUITE);
        }

        for (Pret pret : prets) {
            double mensualite = value(pret.getMensualite());
            double restantAvant = value(pret.getMontantRestant());
            double restantApres = restantAvant - mensualite;

            if (restantApres <= 0) {
                pret.setMontantRestant(0.0);
                pret.setStatut(StatutPret.TERMINE);
            } else {
                pret.setMontantRestant(restantApres);
                pret.setStatut(StatutPret.EN_COURS);
            }
        }

        paie.setStatut(StatutPaie.PAYE);
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
            throw new RuntimeException(
                    "Impossible de valider la paie " + id +
                    " : seul le statut BROUILLON peut être validé"
            );
        }

        paie.setStatut(StatutPaie.VALIDE);
        resultats.add(PaieMapper.toDtoComplet(paie));
    }

    return resultats;
}


@Transactional
public PaieDTO genererPaieForce(PaieGenerationForceDTO request) {

    if (request.getGardienId() == null) {
        throw new RuntimeException("Gardien obligatoire");
    }
    if (request.getMois() == null || request.getMois() < 1 || request.getMois() > 12) {
        throw new RuntimeException("Mois invalide");
    }
    if (request.getAnnee() == null) {
        throw new RuntimeException("Année obligatoire");
    }
    if (request.getDatePaie() == null) {
        throw new RuntimeException("Date de paie obligatoire");
    }

    Gardien gardien = gardienRepository.findById(request.getGardienId())
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

    if (!gardien.isActif()) {
        throw new RuntimeException("Impossible de générer : gardien inactif");
    }

    if (paieRepository.existsByGardienIdAndMoisAndAnnee(
            request.getGardienId(),
            request.getMois(),
            request.getAnnee()
    )) {
        throw new RuntimeException("Une paie existe déjà pour ce gardien sur cette période");
    }

    double salaireBase = value(gardien.getSalaireBase());
    Devise devise = gardien.getDevise();

    if (devise == null) {
        throw new RuntimeException("Devise de salaire absente");
    }

    List<Prime> primes = primeRepository.findPrimesPourPaie(
            gardien.getId(),
            request.getMois(),
            request.getAnnee(),
            StatutPrime.VALIDEE,
            devise
    );

    List<AvanceSalaire> avances = avanceRepository.findAvancesPourPaie(
            gardien.getId(),
            request.getMois(),
            request.getAnnee(),
            StatutAvance.PAYEE,
            devise
    );

    List<Pret> prets = pretRepository.findPretsEnCoursPourPaie(
            gardien.getId(),
            StatutPret.EN_COURS,
            devise
    );

    double totalPrimes = primes.stream().mapToDouble(p -> value(p.getMontant())).sum();
    double totalAvances = avances.stream().mapToDouble(a -> value(a.getMontant())).sum();
    double totalPrets = prets.stream().mapToDouble(p -> value(p.getMensualite())).sum();

    double net = salaireBase + totalPrimes - totalAvances - totalPrets;

    Paie paie = new Paie();
    paie.setGardien(gardien);
    paie.setMois(request.getMois());
    paie.setAnnee(request.getAnnee());
    paie.setDatePaie(request.getDatePaie());
    paie.setSalaireBase(salaireBase);
    paie.setDevise(devise);
    paie.setTotalPrimes(totalPrimes);
    paie.setTotalAvances(totalAvances);
    paie.setTotalPrets(totalPrets);
    paie.setAutresRetenues(0.0);
    paie.setNetAPayer(net);
    paie.setStatut(StatutPaie.BROUILLON);
    paie.setObservation(request.getObservation());

    Paie savedPaie = paieRepository.save(paie);

    List<PaieLigne> lignes = new ArrayList<>();

    lignes.add(PaieLigne.builder()
            .paie(savedPaie)
            .typeLigne(TypeLignePaie.SALAIRE)
            .referenceId(null)
            .libelle("Salaire de base")
            .montant(salaireBase)
            .sens(SensLignePaie.GAIN)
            .build());

    for (Prime p : primes) {
        lignes.add(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.PRIME)
                .referenceId(p.getId())
                .libelle("Prime - " + p.getTypePrime())
                .montant(value(p.getMontant()))
                .sens(SensLignePaie.GAIN)
                .build());
    }

    for (AvanceSalaire a : avances) {
        lignes.add(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.AVANCE)
                .referenceId(a.getId())
                .libelle("Avance sur salaire")
                .montant(value(a.getMontant()))
                .sens(SensLignePaie.RETENUE)
                .build());
    }

    for (Pret pret : prets) {
        lignes.add(PaieLigne.builder()
                .paie(savedPaie)
                .typeLigne(TypeLignePaie.PRET)
                .referenceId(pret.getId())
                .libelle("Mensualité prêt")
                .montant(value(pret.getMensualite()))
                .sens(SensLignePaie.RETENUE)
                .build());
    }

    paieLigneRepository.saveAll(lignes);

    return paieRepository.findByIdWithDetails(savedPaie.getId())
            .map(PaieMapper::toDtoComplet)
            .orElseThrow(() -> new RuntimeException("Paie générée mais introuvable"));
}

}
