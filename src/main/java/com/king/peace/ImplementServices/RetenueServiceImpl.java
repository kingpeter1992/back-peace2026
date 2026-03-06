package com.king.peace.ImplementServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.RetenueRepository;
import com.king.peace.Dto.RetenueDTO;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Retenue;
import com.king.peace.Interfaces.RetenueService;
import com.king.peace.Utiltys.PaieMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class RetenueServiceImpl  implements RetenueService  {
    private final RetenueRepository repository;
        private final GardienRepository employeRepository;



    @Override
    public RetenueDTO save(RetenueDTO dto) {
        Gardien employe = employeRepository.findById(dto.getEmployeId()).orElseThrow();
        Retenue retenue = Retenue.builder()
                .id(dto.getId())
                .gardien(employe)
                .typeRetenue(dto.getTypeRetenue())
                .libelle(dto.getLibelle())
                .montant(dto.getMontant())
                .dateRetenue(dto.getDateRetenue())
                .motif(dto.getMotif())
                .build();
        return PaieMapper.toDto(repository.save(retenue));
    }

    @Override
    public List<RetenueDTO> findAll() {
        return repository.findAll().stream().map(PaieMapper::toDto).toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
}
