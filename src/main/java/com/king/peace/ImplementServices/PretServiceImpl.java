package com.king.peace.ImplementServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dto.PretDTO;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Pret;
import com.king.peace.Interfaces.PretService;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.StatutPret;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PretServiceImpl implements PretService {
    private final PretRepository repository;
    private final GardienRepository employeRepository;

    @Override
    public PretDTO save(PretDTO dto) {
        Gardien employe = employeRepository.findById(dto.getGardienId()).orElseThrow();
        Pret pret = Pret.builder()
                .id(dto.getId())
                .gardien(employe)
                .montantTotal(dto.getMontantTotal())
                .montantRestant(dto.getMontantRestant() == 0 ? dto.getMontantTotal() : dto.getMontantRestant())
                .nombreMois(dto.getNombreMois())
                .mensualite(dto.getMensualite())
                .dateDebut(dto.getDateDebut())
                .devise(dto.getDevise())
                .statut(dto.getStatut() == null ? StatutPret.EN_COURS : dto.getStatut())
                .motif(dto.getMotif())
                .build();
        return PaieMapper.toDto(repository.save(pret));
    }

    @Override
    public List<PretDTO> findAll() {
        return repository.findAll().stream().map(PaieMapper::toDto).toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}