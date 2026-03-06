package com.king.peace.ImplementServices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.AvanceSalaireRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PaiementSalaireRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dao.PrimeRepository;
import com.king.peace.Dao.RetenueRepository;
import com.king.peace.Dto.GenererPaieRequest;
import com.king.peace.Dto.PaiementSalaireDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.PaiementSalaire;
import com.king.peace.Entitys.Pret;
import com.king.peace.Entitys.Prime;
import com.king.peace.Entitys.Retenue;
import com.king.peace.Interfaces.PaiementSalaireService;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.StatutAvance;
import com.king.peace.enums.StatutPaie;
import com.king.peace.enums.StatutPret;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementSalaireServiceImpl implements PaiementSalaireService {

     private final GardienRepository  GardienRepository;
    private final PrimeRepository primeRepository;
    private final RetenueRepository retenueRepository;
    private final AvanceSalaireRepository avanceRepository;
    private final PretRepository pretRepository;
    private final PaiementSalaireRepository paiementRepository;

    @Override
    @Transactional
    public PaiementSalaireDTO genererPaie(GenererPaieRequest request) {

        paiementRepository.findByGardienIdAndMoisAndAnnee(
            request.getGardienId(), request.getMois(), request.getAnnee())
                .ifPresent(p -> { throw new RuntimeException("La paie existe déjà pour cet employé"); });

        Gardien Gardien = GardienRepository.findById(request.getGardienId()).orElseThrow();

        LocalDate debut = LocalDate.of(request.getAnnee(), request.getMois(), 1);
        LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());

double salaireBase = Gardien.getSalaireBase();

double totalPrimes = primeRepository
        .findByGardienIdAndDatePrimeBetween(Gardien.getId(), debut, fin)
        .stream()
        .mapToDouble(Prime::getMontant)
        .sum();

double totalRetenues = retenueRepository
        .findByGardienIdAndDateRetenueBetween(Gardien.getId(), debut, fin)
        .stream()
        .mapToDouble(Retenue::getMontant)
        .sum();

List<AvanceSalaire> avances = avanceRepository
        .findByGardienIdAndStatut(Gardien.getId(), StatutAvance.VALIDEE);

double totalAvances = avances.stream()
        .mapToDouble(AvanceSalaire::getMontant)
        .sum();

List<Pret> prets = pretRepository
        .findByGardienIdAndStatut(Gardien.getId(), StatutPret.EN_COURS);

double totalPret = prets.stream()
        .mapToDouble(Pret::getMensualite)
        .sum();

double salaireBrut = salaireBase + totalPrimes;

double totalDeductions = totalRetenues + totalAvances + totalPret;

double salaireNet = salaireBrut - totalDeductions;

if (salaireNet < 0) {
    salaireNet = 0;
}
salaireNet = Math.round(salaireNet * 100.0) / 100.0;
        PaiementSalaire paiement = PaiementSalaire.builder()
                .gardien(Gardien)
                .mois(request.getMois())
                .annee(request.getAnnee())
                .salaireBase(salaireBase)
                .totalPrimes(totalPrimes)

                .totalRetenues(totalRetenues)
                .totalAvances(totalAvances)
                .totalRemboursementPret(totalPret)
                .salaireBrut(salaireBrut)
                .salaireNet(salaireNet)
                .statut(StatutPaie.BROUILLON)
                .build();

        return PaieMapper.toDto(paiementRepository.save(paiement));
    
}
    
 @Override
    @Transactional
    public PaiementSalaireDTO validerPaie(Long id) {
        PaiementSalaire paiement = paiementRepository.findById(id).orElseThrow();

        List<AvanceSalaire> avances = avanceRepository.findByGardienIdAndStatut(paiement.getGardien().getId(),
         StatutAvance.VALIDEE);
        for (AvanceSalaire avance : avances) {
            avance.setStatut(StatutAvance.DEDUITE);
            avanceRepository.save(avance);
        }

        List<Pret> prets = pretRepository.findByGardienIdAndStatut(paiement.getGardien().getId(), 
        StatutPret.EN_COURS);
        for (Pret pret : prets) {
        
          double restant = pret.getMontantRestant() - pret.getMensualite();

            if (restant <= 0) {
                pret.setMontantRestant(0);
                pret.setStatut(StatutPret.TERMINE);
            } else {
                pret.setMontantRestant(restant);
            }

            pretRepository.save(pret);
        }

        paiement.setStatut(StatutPaie.VALIDE);
        paiement.setDatePaiement(LocalDate.now());
        return PaieMapper.toDto(paiementRepository.save(paiement));
    }

    @Override
    public List<PaiementSalaireDTO> findAll() {
        return paiementRepository.findAll().stream().map(PaieMapper::toDto).toList();
    }

}
