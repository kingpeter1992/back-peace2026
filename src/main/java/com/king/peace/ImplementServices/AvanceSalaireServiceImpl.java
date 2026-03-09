package com.king.peace.ImplementServices;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.peace.Dao.AvanceSalaireRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dto.AvanceSalaireDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Interfaces.AvanceSalaireService;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.StatutAvance;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvanceSalaireServiceImpl  implements AvanceSalaireService {
    
    private final AvanceSalaireRepository repository;
    private final GardienRepository employeRepository;

    @Override
    public AvanceSalaireDTO save(AvanceSalaireDTO dto) {
        Gardien employe = employeRepository.findById(dto.getGardienId()).orElseThrow();
        AvanceSalaire avance = AvanceSalaire.builder()
                .id(dto.getId())
                .gardien(employe)
                .montant(dto.getMontant())
                .dateAvance(dto.getDateAvance())
                .devise(dto.getDevise())
                .statut(dto.getStatut() == null ? StatutAvance.EN_ATTENTE : dto.getStatut())
                .observation(dto.getObservation())
                .build();
        return PaieMapper.toDto(repository.save(avance));
    }


     @Transactional
public AvanceSalaireDTO create(AvanceSalaireDTO dto) {
    Gardien gardien = employeRepository.findById(dto.getGardienId())
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

    if (dto.getMontant() == 0 || dto.getMontant() <= 0) {
        throw new RuntimeException("Montant invalide");
    }

    if (dto.getDevise() == null) {
        throw new RuntimeException("Devise obligatoire");
    }

    if (dto.getDateAvance() == null) {
        throw new RuntimeException("Date avance obligatoire");
    }

    if (dto.getMoisConcerne() == null || dto.getAnneeConcerne() == null) {
        throw new RuntimeException("Mois et année concernés sont obligatoires");
    }

    if (dto.getMotif() == null || dto.getMotif().trim().isEmpty()) {
        throw new RuntimeException("Motif obligatoire");
    }

    StatutAvance statut = dto.getStatut() != null ? dto.getStatut() : StatutAvance.EN_ATTENTE;

    // contrôle uniquement si l'avance est PAYEE
    if (statut == StatutAvance.PAYEE) {
        double salaireBase = gardien.getSalaireBase() != 0 ? gardien.getSalaireBase() : 0.0;

        Double totalAvancesPayees = repository.sumMontantByGardienAndMoisAndAnneeAndStatut(
                gardien.getId(),
                dto.getMoisConcerne(),
                dto.getAnneeConcerne(),
                StatutAvance.PAYEE
        );

        double totalExistant = totalAvancesPayees != null ? totalAvancesPayees : 0.0;
        double totalAvecNouvelleAvance = totalExistant + dto.getMontant();

        if (totalAvecNouvelleAvance > salaireBase) {
            throw new RuntimeException(
                    "Le total des avances PAYEE pour ce gardien au mois "
                            + dto.getMoisConcerne() + "/" + dto.getAnneeConcerne()
                            + " dépasse le salaire de base. "
                            + "Salaire de base=" + salaireBase
                            + ", total existant=" + totalExistant
                            + ", nouvelle avance=" + dto.getMontant()
            );
        }
    }

    AvanceSalaire a = new AvanceSalaire();
    a.setGardien(gardien);
    a.setMontant(dto.getMontant());
    a.setDevise(dto.getDevise());
    a.setDateAvance(dto.getDateAvance());
    a.setMotif(dto.getMotif());
    a.setStatut(statut);
    a.setMoisConcerne(dto.getMoisConcerne());
    a.setAnneeConcerne(dto.getAnneeConcerne());
    a.setObservation(dto.getObservation());

    return PaieMapper.toDto(repository.save(a));
}


  
    public AvanceSalaireDTO deduire(Long id) {
        AvanceSalaire a = repository.findByIdWithGardien(id)
                .orElseThrow(() -> new RuntimeException("Avance introuvable"));

        a.setStatut(StatutAvance.DEDUITE);
        repository.save(a);
        
        return PaieMapper.toDto(a);
    }

    @Override
    public List<AvanceSalaireDTO> findAll() {
        return repository.findAll().stream().map(PaieMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AvanceSalaireDTO> findAllGardien() {
        return repository.findAllWithGardien()
                .stream()
                .map(PaieMapper::toDto)
                .toList();
    }

    @Override
@Transactional
public AvanceSalaireDTO valider(Long id) {
    AvanceSalaire avance = repository.findByIdWithGardien(id)
            .orElseThrow(() -> new RuntimeException("Avance introuvable"));

    avance.setStatut(StatutAvance.VALIDEE);

    return PaieMapper.toDto(avance);
}

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }


@Transactional
public AvanceSalaireDTO update(Long id, AvanceSalaireDTO dto) {
    AvanceSalaire avance = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Avance salaire introuvable"));

    Gardien gardien = employeRepository.findById(dto.getGardienId())
            .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

    StatutAvance statut = dto.getStatut() != null ? dto.getStatut() : avance.getStatut();

    if (statut == StatutAvance.PAYEE) {
        double salaireBase = gardien.getSalaireBase() != 0 ? gardien.getSalaireBase() : 0.0;

        Double totalAvancesPayees = repository.sumMontantByGardienAndMoisAndAnneeAndStatutAndIdNot(
                gardien.getId(),
                dto.getMoisConcerne(),
                dto.getAnneeConcerne(),
                StatutAvance.PAYEE,
                id
        );

        double totalExistant = totalAvancesPayees != null ? totalAvancesPayees : 0.0;
        double totalAvecNouvelleAvance = totalExistant + dto.getMontant();

        if (totalAvecNouvelleAvance > salaireBase) {
            throw new RuntimeException("Le total des avances PAYEE du même mois dépasse le salaire de base");
        }
    }

    avance.setGardien(gardien);
    avance.setMontant(dto.getMontant());
    avance.setDevise(dto.getDevise());
    avance.setDateAvance(dto.getDateAvance());
    avance.setMotif(dto.getMotif());
    avance.setStatut(statut);
    avance.setMoisConcerne(dto.getMoisConcerne());
    avance.setAnneeConcerne(dto.getAnneeConcerne());
    avance.setObservation(dto.getObservation());

    return PaieMapper.toDto(repository.save(avance));
}

   
}
