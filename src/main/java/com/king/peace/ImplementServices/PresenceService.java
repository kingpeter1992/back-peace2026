package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PointageRepository;
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

    System.out.println(dto.getDate());
    System.out.println(dto.getDatesortie());


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


}
