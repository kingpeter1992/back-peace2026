package com.king.peace.ImplementServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PrimeRepository;
import com.king.peace.Dto.PrimeDTO;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Prime;
import com.king.peace.Interfaces.PrimeService;
import com.king.peace.Utiltys.PaieMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrimeServiceImpl  implements PrimeService {

       private final PrimeRepository repository;
       private final GardienRepository gardienRepository;


    
  @Override
    public PrimeDTO save(PrimeDTO dto) {
        Gardien employe = gardienRepository.findById(dto.getEmployeId()).orElseThrow();
        Prime prime = Prime.builder()
                .id(dto.getId())
                .gardien(employe)
                .typePrime(dto.getTypePrime())
                .libelle(dto.getLibelle())
                .montant(dto.getMontant())
                .datePrime(dto.getDatePrime())
                .observation(dto.getObservation())
                .build();
        return PaieMapper.toDto(repository.save(prime));
    }

    @Override
    public List<PrimeDTO> findAll() {
        return repository.findAll().stream().map(PaieMapper::toDto).toList();
    }
  @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
