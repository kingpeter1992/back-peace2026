package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PointageRepository;
import com.king.peace.Dto.GardienPresenceSalaireDto;
import com.king.peace.Dto.PointageDto;
import com.king.peace.Dto.PresenceDto;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Pointage;
import com.king.peace.Entitys.StatutPointage;
import com.king.peace.Utiltys.PresenceMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PresenceService {

     private final PointageRepository pointageRepository;
     private final GardienRepository gardienRepository;

  

    public PresenceService(PointageRepository pointageRepository, GardienRepository gardienRepository) {
        this.pointageRepository = pointageRepository;
        this.gardienRepository = gardienRepository;
    }

    // Retourne tous les pointages en PresenceDto[]
    public List<PresenceDto> getAllPointages() {
        
        List<Pointage> pointages = pointageRepository.findAll();
        return pointages.stream()
                .map(PresenceMapper::toDto)
                .collect(Collectors.toList());
    }

    // Optionnel : filtrer par gardien
    public List<PresenceDto> getPointagesByGardien(Long gardienId) {
        return pointageRepository.findByGardienId(gardienId).stream()
                .map(PresenceMapper::toDto)
                .collect(Collectors.toList());
    }

    
public List<Pointage> pointerGardiensMasse(List<Long> gardienIds, LocalDate date, StatutPointage statut) {
    List<Pointage> pointages = new ArrayList<>();

    for (Long gardienId : gardienIds) {
        Gardien g = gardienRepository.findById(gardienId)
                .orElseThrow(() -> new RuntimeException("Gardien non trouvé"));

        Pointage p = Pointage.builder()
                .gardien(g)
                .date(date)
                .statut(statut)
                .build();

        pointages.add(p);
    }

    return pointageRepository.saveAll(pointages);
}

public void savePointage(PointageDto dto) {

    Pointage pointage = new Pointage();
    
    // Récupérer le gardien
    Gardien gardien;
    try {
        gardien = gardienRepository.getReferenceById(dto.getGardienId());
    } catch (EntityNotFoundException e) {
        throw new RuntimeException("Gardien introuvable avec id " + dto.getGardienId());
    }

    pointage.setGardien(gardien);
    pointage.setDate(dto.getDate());
    pointage.setDatesortie(dto.getDatesortie());
    pointage.setHeureEntree(dto.getHeureEntree());
    pointage.setHeureSortie(dto.getHeureSortie());
//    pointage.setSite(dto.getSite());

    try {
        pointage.setStatut(StatutPointage.valueOf(dto.getStatut()));
    } catch (IllegalArgumentException e) {
        pointage.setStatut(StatutPointage.PRESENT);
    }

    pointageRepository.save(pointage);
}

 public List<GardienPresenceSalaireDto> getPresenceSalaireParPeriode(
        LocalDate dateDebut,
        LocalDate dateFin
) {

    List<Gardien> gardiens = gardienRepository.findActifsAvecPresenceDansPeriode(
            dateDebut,
            dateFin
    );

    return gardiens.stream()
            .map(g -> {
                int joursAPrester = g.getNbrjours() != null ? g.getNbrjours() : 0;
                double salaireBase = g.getSalaireBase();

                double montantParJour = joursAPrester > 0
                        ? arrondir(salaireBase / joursAPrester)
                        : 0.0;

                List<Pointage> pointages = pointageRepository.findByGardienIdAndPeriodeAndStatuts(
                        g.getId(),
                        List.of(StatutPointage.PRESENT),
                        dateDebut,
                        dateFin
                );

                long joursPresents = compterJoursDistincts(pointages, dateDebut, dateFin);

                double montantTotalGagne = arrondir(montantParJour * joursPresents);

                String nomComplet = ((g.getNom() != null ? g.getNom() : "") + " " +
                        (g.getPrenom() != null ? g.getPrenom() : "")).trim();

                return new GardienPresenceSalaireDto(
                        g.getId(),
                        nomComplet,
                        dateDebut,
                        dateFin,
                        joursAPrester,
                        salaireBase,
                        montantParJour,
                        joursPresents,
                        montantTotalGagne,
                        g.getDevise()
                );
            })
            .filter(dto -> dto.getNbrJoursPresents() > 0)
            .toList();
}

private long compterJoursDistincts(List<Pointage> pointages, LocalDate dateDebut, LocalDate dateFin) {
    Set<LocalDate> joursDistincts = new HashSet<>();

    for (Pointage p : pointages) {
        LocalDate debut = p.getDate();
        LocalDate fin = p.getDatesortie();

        if (debut == null) {
            continue;
        }

        if (fin == null) {
            fin = debut;
        }

        // on coupe la période du pointage à l’intervalle demandé
        LocalDate debutEffectif = debut.isBefore(dateDebut) ? dateDebut : debut;
        LocalDate finEffectif = fin.isAfter(dateFin) ? dateFin : fin;

        // sécurité
        if (debutEffectif.isAfter(finEffectif)) {
            continue;
        }

        LocalDate courant = debutEffectif;
        while (!courant.isAfter(finEffectif)) {
            joursDistincts.add(courant);
            courant = courant.plusDays(1);
        }
    }

    return joursDistincts.size();
}

private double arrondir(double valeur) {
    return Math.round(valeur * 100.0) / 100.0;
}
}
