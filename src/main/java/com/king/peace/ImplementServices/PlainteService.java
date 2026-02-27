package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PlainteRepository;
import com.king.peace.Dao.ReponsePlainteRepository;
import com.king.peace.Dto.PlainteDto;
import com.king.peace.Dto.ReponseDto;
import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.NiveauPlainte;
import com.king.peace.Entitys.Plaintes;
import com.king.peace.Entitys.ReponsePlainte;
import com.king.peace.Entitys.StatutAffectation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlainteService {

    private final PlainteRepository plainteRepository;
    private final ClientRepository clientRepository;
    private final GardienRepository gardienRepository;
    private final ReponsePlainteRepository reponsePlainteRepository;
    private final AffectationService affectationRepository;
    private final ContratRepository contratRepository;

    // 🔹 CREATE
    public PlainteDto create(PlainteDto dto) {
        if (dto.getClientId() == null)
            throw new RuntimeException("Client obligatoire");
        if (dto.getGardienId() == null)
            throw new RuntimeException("Gardien obligatoire");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new RuntimeException("Description obligatoire");
        if (dto.getNote() == null)
            throw new RuntimeException("Note obligatoire");

        // Vérifier que client existe
        var client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        // Vérifier que gardien existe
        var gardien = gardienRepository.findById(dto.getGardienId())
                .orElseThrow(() -> new RuntimeException("Gardien introuvable id=" + dto.getGardienId()));

        // Vérifier affectation ACTIVE
        boolean affecte = affectationRepository.existsByGardienIdAndContrat_Client_IdAndStatut(
                dto.getGardienId(),
                dto.getClientId(),
                StatutAffectation.ACTIVE);

        if (!affecte) {
            throw new RuntimeException("Impossible : gardien " + dto.getGardienId()
                    + " non affecté ACTIVE au client " + dto.getClientId());
        }

        Plaintes plainte = new Plaintes();
        plainte.setDescription(dto.getDescription());
        plainte.setNote(dto.getNote());
        plainte.setNiveau(calculerNiveau(dto.getNote()));
        plainte.setDatePlainte(LocalDate.now());
        plainte.setDateLimiteReponse(LocalDate.now().plusDays(3));
        plainte.setCreatedAt(LocalDate.now());

        plainte.setClient(client);
        plainte.setGardien(gardien);

        return mapToDTO(plainteRepository.save(plainte));
    }

    // 🔹 READ ALL
    public List<PlainteDto> getAll() {
        return plainteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // 🔹 DELETE
    public void delete(Long id) {
        plainteRepository.deleteById(id);
    }

    // 🔹 Calcul automatique
    private NiveauPlainte calculerNiveau(int note) {

        if (note <= 3)
            return NiveauPlainte.SANS_PROBLEME;
        else if (note <= 5)
            return NiveauPlainte.ASSEZ_PROBLEMATIQUE;
        else if (note <= 8)
            return NiveauPlainte.PROBLEMATIQUE;
        else
            return NiveauPlainte.TRES_PROBLEMATIQUE;
    }

    private PlainteDto mapToDTO(Plaintes p) {
        PlainteDto dto = new PlainteDto();

        dto.setId(p.getId());
        dto.setDescription(p.getDescription());
        dto.setNote(p.getNote());
        dto.setNiveau(p.getNiveau());
        dto.setDatePlainte(p.getDatePlainte());
        dto.setDateLimiteReponse(p.getDateLimiteReponse());
        dto.setReponseGardien(p.getReponseGardien());
        dto.setRepondu(p.isRepondu());
        dto.setClientId(p.getClient().getId());
        dto.setGardienId(p.getGardien().getId());
        dto.setActive(p.isActive());
        dto.setClientNom(p.getClient().getNom());
        dto.setGardienNom(p.getGardien().getNom());
        dto.setActive(p.isActive());

        return dto;
    }

    public ReponseDto ajouterReponse(Long plainteId, ReponseDto dto) {

        Plaintes plainte = plainteRepository.findById(plainteId)
                .orElseThrow(() -> new RuntimeException("Plainte non trouvée"));

        if (!plainte.isActive()) {
            throw new RuntimeException("Plainte clôturée, impossible de répondre");
        }

        // Création de la réponse
        ReponsePlainte reponse = new ReponsePlainte();
        reponse.setReponse(dto.getReponse());
        reponse.setDateReponse(LocalDateTime.now());
        reponse.setPlainte(plainte);

        reponsePlainteRepository.save(reponse);

        return ReponseDto.builder()
                .id(reponse.getId())
                .reponse(reponse.getReponse())
                .dateReponse(reponse.getDateReponse())
                .build();

    }

    public List<ReponsePlainte> listerReponses(Long plainteId) {
        return reponsePlainteRepository.findByPlainteId(plainteId);
    }

    public PlainteDto update(Long id, PlainteDto dto) {

        Plaintes plainte = plainteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plainte introuvable"));

        // Mise à jour des champs simples
        plainte.setDescription(dto.getDescription());
        plainte.setNote(dto.getNote());
        plainte.setActive(dto.isActive());
        plainte.setDatePlainte(dto.getDatePlainte());

        // Mise à jour des relations
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Gardien gardien = gardienRepository.findById(dto.getGardienId())
                .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

        plainte.setClient(client);
        plainte.setGardien(gardien);

        // Calcul automatique du niveau
        plainte.setNiveau(calculerNiveau(dto.getNote()));

        plainteRepository.save(plainte);

        return mapToDTO(plainte);
    }

    @Transactional(readOnly = true)
    public List<PlainteDto> getAllPlainteWithResponse() {

        return plainteRepository.findAllWithReponses()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private PlainteDto mapToDto(Plaintes plainte) {

        return PlainteDto.builder()
                .id(plainte.getId())
                .description(plainte.getDescription())
                .datePlainte(plainte.getDatePlainte())
                .statut(plainte.getStatut())
                .note(plainte.getNote())
                .niveau(plainte.getNiveau())
                .dateLimiteReponse(plainte.getDateLimiteReponse())
                .reponseGardien(plainte.getReponseGardien())
                .repondu(plainte.isRepondu())
                .active(plainte.isActive())

                .clientNom(
                        plainte.getClient() != null
                                ? plainte.getClient().getNom()
                                : null)

                .gardienNom(
                        plainte.getGardien() != null
                                ? plainte.getGardien().getNom()
                                : null)

                .clientId(
                        plainte.getClient() != null
                                ? plainte.getClient().getId()
                                : null)

                .gardienId(
                        plainte.getGardien() != null
                                ? plainte.getGardien().getId()
                                : null)

                .listeReponses(
                        plainte.getReponses() != null
                                ? plainte.getReponses()
                                        .stream()
                                        .map(r -> ReponseDto.builder()
                                                .id(r.getId())
                                                .reponse(r.getReponse())
                                                .dateReponse(r.getDateReponse())
                                                .build())
                                        .sorted((a, b) -> b.getDateReponse().compareTo(a.getDateReponse())) // tri
                                                                                                            // récent en
                                                                                                            // premier
                                        .toList()
                                : List.of())

                .build();
    }

    @Transactional(readOnly = true)
    public PlainteDto getRespnseById(Long id) {
        Plaintes plainte = plainteRepository
                .findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Plainte non trouvée"));

        return mapToDto(plainte);
    }

}