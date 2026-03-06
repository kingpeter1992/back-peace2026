package com.king.peace.ImplementServices;


import java.util.List;

import org.springframework.stereotype.Service;

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
        Gardien employe = employeRepository.findById(dto.getEmployeId()).orElseThrow();
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

    @Override
    public List<AvanceSalaireDTO> findAll() {
        return repository.findAll().stream().map(PaieMapper::toDto).toList();
    }

    @Override
    public AvanceSalaireDTO valider(Long id) {
        AvanceSalaire avance = repository.findById(id).orElseThrow();
        avance.setStatut(StatutAvance.VALIDEE);
        return PaieMapper.toDto(repository.save(avance));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
