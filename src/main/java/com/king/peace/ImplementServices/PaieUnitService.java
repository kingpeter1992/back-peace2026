package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.king.peace.Dao.AvanceSalaireRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PaieLigneRepository;
import com.king.peace.Dao.PaieRepository;
import com.king.peace.Dao.PointageRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dao.PrimeRepository;
import com.king.peace.Dto.PaieDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Paie;
import com.king.peace.Entitys.PaieLigne;
import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.Pret;
import com.king.peace.Entitys.Prime;
import com.king.peace.Entitys.StatutPointage;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.SensLignePaie;
import com.king.peace.enums.StatutAvance;
import com.king.peace.enums.StatutPaie;
import com.king.peace.enums.StatutPret;
import com.king.peace.enums.StatutPrime;
import com.king.peace.enums.TypeLignePaie;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaieUnitService {

    private final GardienRepository gardienRepository;
    private final PaieRepository paieRepository;
    private final PointageRepository pointageRepositor;
    private final PrimeRepository primeRepository;
    private final AvanceSalaireRepository avanceRepository;
    private final PretRepository pretRepository;
    private final PaieLigneRepository paieLigneRepository;


private String generateNumeroBulletin(Long gardienId, Integer mois, Integer annee) {
        return "PAIE-" + gardienId + "-" + mois + "-" + annee;
    }
    
@Transactional(propagation = Propagation.REQUIRES_NEW)
public PaieDTO genererPaieUnitaire(Long gardienId, LocalDate dateDebut, LocalDate dateFin) {

    validatePeriode(gardienId, dateDebut, dateFin);

    Integer mois = dateDebut.getMonthValue();
    Integer annee = dateDebut.getYear();

    Gardien gardien = gardienRepository.findById(gardienId)
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

    validateGardien(gardien);

    if (paieRepository.existsByGardienIdAndDatePaieDebutAndDatePaieFin(gardienId, dateDebut, dateFin)) {
        throw new RuntimeException("Paie déjà générée pour cette période");
    }

    Devise devise = gardien.getDevise();

    List<Pointage> pointagesList = pointageRepositor.findByGardienIdAndPeriodeAndStatuts(
            gardienId,
            List.of(StatutPointage.PRESENT, StatutPointage.MISSION),
            dateDebut,
            dateFin
    );

    if (pointagesList == null || pointagesList.isEmpty()) {
        throw new RuntimeException("Aucun pointage trouvé pour ce gardien sur la période demandée");
    }

    int joursPrestes = calculerJoursPrestes(pointagesList, dateDebut, dateFin);
    System.out.println("Nombre de jours prestés : " + joursPrestes);

    if (joursPrestes <= 0) {
        throw new RuntimeException("Nombre de jours prestés invalide");
    }

    if (joursPrestes > gardien.getNbrjours()) {
        throw new RuntimeException("Le nombre de jours prestés dépasse le nombre de jours autorisés");
    }

    double salaireBase = (gardien.getSalaireBase() / gardien.getNbrjours()) * joursPrestes;
    System.out.println("Salaire de base : " + salaireBase);
    salaireBase = Math.round(salaireBase * 100.0) / 100.0;
    System.out.println("Salaire de base arrondi : " + salaireBase);
    

    List<Prime> primes = primeRepository.findPrimesPourPaieParPeriode(
            gardienId, dateDebut, dateFin, StatutPrime.VALIDEE, devise
    );

    List<AvanceSalaire> avances = avanceRepository.findAvancesPourPaieParPeriode(
            gardienId, dateDebut, dateFin, StatutAvance.VALIDEE, devise
    );

    List<Pret> prets = pretRepository.findPretsEnCoursPourPaieParPeriode(
            gardienId, dateFin, StatutPret.EN_COURS, devise
    );

    double totalPrimes = primes.stream().mapToDouble(p -> p.getMontant() != 0 ? p.getMontant() : 0.0).sum();
        System.out.println("Total des montants des primes : " + totalPrimes);

    double totalAvances = avances.stream().mapToDouble(a -> a.getMontant() != 0 ? a.getMontant() : 0.0).sum();
        System.out.println("Total des montants des avances : " + totalAvances);

    double totalPrets = prets.stream().mapToDouble(p -> p.getMensualite() != 0 ? p.getMensualite() : 0.0).sum();
        System.out.println("Total des montants des prêts : " + totalPrets);

    double net = salaireBase + totalPrimes - totalAvances - totalPrets;
    if (net < 0) {
        net = 0;
    }

    net = Math.round(net * 100.0) / 100.0;
    System.out.println("Net à payer : " + net);

    Paie paie = new Paie();
    paie.setGardien(gardien);
    paie.setDatePaieDebut(dateDebut);
    paie.setDatePaieFin(dateFin);
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

    saveLignesSalaire(savedPaie, salaireBase);
    saveLignesPrimes(savedPaie, primes);
    saveLignesAvances(savedPaie, avances);
    saveLignesPrets(savedPaie, prets);

    return PaieMapper.toDtoComplet(savedPaie);
}
private void saveLignesPrets(Paie savedPaie, List<Pret> prets) {
    for (Pret pret : prets) {
        double mensualite = pret.getMensualite() != 0 ? pret.getMensualite() : 0.0;

        paieLigneRepository.save(PaieLigne.builder()
                .paies(savedPaie)
                .typeLigne(TypeLignePaie.PRET)
                .referenceId(pret.getId())
                .libelle("Mensualité prêt")
                .montant(mensualite)
                .sens(SensLignePaie.RETENUE)
                .build());
    }
}
private void saveLignesAvances(Paie savedPaie, List<AvanceSalaire> avances) {
    for (AvanceSalaire a : avances) {
        paieLigneRepository.save(PaieLigne.builder()
                .paies(savedPaie)
                .typeLigne(TypeLignePaie.AVANCE)
                .referenceId(a.getId())
                .libelle("Avance sur salaire")
                .montant(a.getMontant())
                .sens(SensLignePaie.RETENUE)
                .build());
   //     a.setStatut(StatutAvance.DEDUITE);
    }
    avanceRepository.saveAll(avances);
}
private void saveLignesPrimes(Paie savedPaie, List<Prime> primes) {
    for (Prime p : primes) {
        paieLigneRepository.save(PaieLigne.builder()
                .paies(savedPaie)
                .typeLigne(TypeLignePaie.PRIME)
                .referenceId(p.getId())
                .libelle("Prime - " + p.getTypePrime())
                .montant(p.getMontant())
                .sens(SensLignePaie.GAIN)
                .build());
      //  p.setStatut(StatutPrime.PAYEE);
    }
    primeRepository.saveAll(primes);
}
private void saveLignesSalaire(Paie savedPaie, double salaireBase) {
    paieLigneRepository.save(PaieLigne.builder()
            .paies(savedPaie)
            .typeLigne(TypeLignePaie.SALAIRE)
            .referenceId(null)
            .libelle("Salaire de base")
            .montant(salaireBase)
            .sens(SensLignePaie.GAIN)
            .build());
}
private void validatePeriode(Long gardienId, LocalDate dateDebut, LocalDate dateFin) {
    if (gardienId == null) {
        throw new RuntimeException("Le gardien est obligatoire");
    }
    if (dateDebut == null || dateFin == null) {
        throw new RuntimeException("La période est obligatoire");
    }
    if (dateFin.isBefore(dateDebut)) {
        throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
    }
}

private void validateGardien(Gardien gardien) {
    if (!gardien.isActif()) {
        throw new RuntimeException("Gardien inactif");
    }
    if (gardien.getDevise() == null) {
        throw new RuntimeException("Devise de salaire absente");
    }
    if (gardien.getSalaireBase() <= 0) {
        throw new RuntimeException("Salaire de base invalide");
    }
    if (gardien.getNbrjours() == null || gardien.getNbrjours() <= 0) {
        throw new RuntimeException("Nombre de jours de travail invalide");
    }
}

private int calculerJoursPrestes(List<Pointage> pointagesList, LocalDate dateDebut, LocalDate dateFin) {
    Set<LocalDate> joursUniques = pointagesList.stream()
            .flatMap(p -> {
                if (p.getDate() == null || p.getDatesortie() == null) {
                    throw new RuntimeException("Date d'entrée ou date de sortie manquante dans un pointage");
                }

                if (p.getDatesortie().isBefore(p.getDate())) {
                    throw new RuntimeException("La date de sortie ne peut pas être avant la date d'entrée");
                }

                LocalDate debut = p.getDate().isBefore(dateDebut) ? dateDebut : p.getDate();
                LocalDate fin = p.getDatesortie().isAfter(dateFin) ? dateFin : p.getDatesortie();

                if (fin.isBefore(debut)) {
                    return Stream.<LocalDate>empty();
                }

                return debut.datesUntil(fin.plusDays(1));
            })
            .collect(Collectors.toSet());

    return joursUniques.size();
}
}