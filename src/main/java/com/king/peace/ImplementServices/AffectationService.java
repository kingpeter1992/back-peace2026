package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.king.peace.Dao.AffectationRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dto.AffectationDto;
import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.StatutAffectation;
import com.king.peace.Entitys.StatutGardien;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AffectationService {
    
    private final AffectationRepository affectationRepository;
    private final GardienRepository gardienRepository;
    private final ContratRepository contratRepository;

    public AffectationService(AffectationRepository affectationRepository, GardienRepository gardienRepository,
            ContratRepository contratRepository) {
        this.affectationRepository = affectationRepository;
        this.gardienRepository = gardienRepository;
        this.contratRepository = contratRepository;
    }

    // ================= GET ALL =================
    public List<AffectationDto> findAllAffectation() {
    List<Affectation> affectations = affectationRepository.findAll(); // <-- juste findAll()
        return affectations.stream()
                .map(a -> AffectationDto.builder()
                        .id(a.getId())
                        .dateDebut(a.getContrat().getDateDebut())
                        .dateFin(a.getContrat().getDateFin())
                        .gardienId(a.getGardien().getId())
                        .contratId(a.getContrat().getId())
                        .statut(a.getStatut())
                        .dateAffectation(a.getDateAffectation())
                        .description(a.getDescription())
                        .active(a.isActive())
                        .refContrats(a.getContrat().getRefContrats())
                        .build())
                .collect(Collectors.toList());
    }

    // ================= FIND BY ID =================
    public AffectationDto findById(Long id) {
        Affectation a = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found"));
        return AffectationDto.builder()
                .id(a.getId())
                .dateDebut(a.getContrat().getDateDebut())
                .dateFin(a.getContrat().getDateFin())
                .gardienId(a.getGardien().getId())
                .contratId(a.getContrat().getId())
                .statut(a.getStatut())
                .dateAffectation(a.getDateAffectation())
                .description(a.getDescription())
                .active(a.isActive())
                .refContrats(a.getContrat().getRefContrats())
                .build();
    }

    // ================= CREATE / AFFECT =================
 public AffectationDto affecterGardien(AffectationDto dto) {

    // Vérifier si le gardien existe
    Gardien gardien = gardienRepository.findById(dto.getGardienId())
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

    // Vérifier si le contrat existe
    Contrats contrat = contratRepository.findById(dto.getContratId())
            .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

    // Vérification si gardien et contrat sont actifs
    if (!Boolean.TRUE.equals(gardien.isActif()) || !Boolean.TRUE.equals(contrat.isActive())) {
        throw new RuntimeException("Gardien ou contrat non actif");
    }

    // Vérifier si le gardien a déjà une affectation active
    boolean hasActiveAffectation = affectationRepository
            .existsByGardienAndActiveTrue(gardien);

    if (hasActiveAffectation) {
        throw new RuntimeException("Le gardien a déjà une affectation active");
    }


    Long nombreAffecte = affectationRepository.countActiveByContratId(contrat.getId());

if (nombreAffecte >= contrat.getNombreGardiens()) {
    throw new RuntimeException("Le nombre d'affectations pour ce contrat est limité");
}


    // Créer l'affectation
    Affectation a = Affectation.builder()
            .gardien(gardien)
            .contrat(contrat)
            .dateAffectation(LocalDate.now())
            .dateDebut(contrat.getDateDebut())
            .dateFin(contrat.getDateFin())
            .statut(dto.getStatut() != null ? dto.getStatut() : StatutAffectation.ACTIVE)
            .description(dto.getDescription())
            .active(true)
            .site(contrat.getZone())
            .build();

    // Sauvegarder
    Affectation saved = affectationRepository.save(a);

    // Retourner le DTO
    return AffectationDto.builder()
            .id(saved.getId())
            .dateDebut(saved.getContrat().getDateDebut())
            .dateFin(saved.getContrat().getDateFin())
            .gardienId(saved.getGardien().getId())
            .contratId(saved.getContrat().getId())
            .statut(saved.getStatut())
            .dateAffectation(saved.getDateAffectation())
            .description(saved.getDescription())
            .active(saved.isActive())
            .refContrats(saved.getContrat().getRefContrats())
            .build();
}

    // ================= DELETE =================
    public void deleteAffectation(Long id) {
        Affectation a = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found"));
        affectationRepository.delete(a);
    }

    // ================= UPDATE =================
    public AffectationDto updateAffectation(Long id, AffectationDto dto) {
        Affectation a = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        a.setDescription(dto.getDescription());
        a.setStatut(dto.getStatut());
        a.setActive(dto.isActive());
        a.setDateDebut(dto.getDateDebut());
        a.setDateFin(dto.getDateFin());


        Affectation saved = affectationRepository.save(a);

        return AffectationDto.builder()
                .id(saved.getId())
                .dateDebut(saved.getContrat().getDateDebut())
                .dateFin(saved.getContrat().getDateFin())
                .gardienId(saved.getGardien().getId())
                .contratId(saved.getContrat().getId())
                .statut(saved.getStatut())
                .dateAffectation(saved.getDateAffectation())

                .description(saved.getDescription())
                .active(saved.isActive())
                .refContrats(saved.getContrat().getRefContrats())
                .build();
    }



    public boolean existsByGardienIdAndContrat_Client_IdAndStatut(Long gardienId, Long clientId,
            StatutAffectation active) {
                return affectationRepository.existsByGardienIdAndContrat_Client_IdAndStatut(gardienId, clientId, active);
    }

    public Long countActiveByContratId(Long id) {

        return affectationRepository.countActiveByContratId(id);
    }

   
}

