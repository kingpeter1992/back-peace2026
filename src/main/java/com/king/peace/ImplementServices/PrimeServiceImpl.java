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
import com.king.peace.enums.StatutPrime;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrimeServiceImpl  implements PrimeService {

       private final PrimeRepository repository;
       private final GardienRepository employeRepository;

 @Transactional
    public PrimeDTO create(PrimeDTO dto) {
        Gardien gardien = employeRepository.findById(dto.getGardienId())
                .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

        if (dto.getMontant() == null || dto.getMontant() <= 0) {
            throw new RuntimeException("Montant invalide");
        }
        if (dto.getDevise() == null) {
            throw new RuntimeException("Devise obligatoire");
        }
        if (dto.getDatePrime() == null) {
            throw new RuntimeException("Date prime obligatoire");
        }
        if (dto.getTypePrime() == null) {
            throw new RuntimeException("Type prime obligatoire");
        }
        if (dto.getMotif() == null || dto.getMotif().trim().isEmpty()) {
            throw new RuntimeException("Motif obligatoire");
        }
        if (dto.getMoisConcerne() == null || dto.getAnneeConcerne() == null) {
            throw new RuntimeException("Mois et année concernés obligatoires");
        }

        Prime p = new Prime();
        p.setGardien(gardien);
        p.setMontant(dto.getMontant());
        p.setDevise(dto.getDevise());
        p.setDatePrime(dto.getDatePrime());
        p.setTypePrime(dto.getTypePrime());
        p.setMotif(dto.getMotif().trim());
        p.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutPrime.EN_ATTENTE);
        p.setMoisConcerne(dto.getMoisConcerne());
        p.setAnneeConcerne(dto.getAnneeConcerne());
        p.setObservation(dto.getObservation());

        return PaieMapper.toDto(repository.save(p));
    }

    @Transactional
    public PrimeDTO update(Long id, PrimeDTO dto) {
        Prime p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prime introuvable"));

        Gardien gardien = employeRepository.findById(dto.getGardienId())
                .orElseThrow(() -> new RuntimeException("Gardien introuvable"));

        p.setGardien(gardien);
        p.setMontant(dto.getMontant());
        p.setDevise(dto.getDevise());
        p.setDatePrime(dto.getDatePrime());
        p.setTypePrime(dto.getTypePrime());
        p.setMotif(dto.getMotif());
        p.setMoisConcerne(dto.getMoisConcerne());
        p.setAnneeConcerne(dto.getAnneeConcerne());
        p.setObservation(dto.getObservation());

        if (dto.getStatut() != null) {
            p.setStatut(dto.getStatut());
        }

        return PaieMapper.toDto(repository.save(p));
    }

    @Transactional
    public PrimeDTO valider(Long id) {
        Prime p = repository.findByIdWithGardien(id)
                .orElseThrow(() -> new RuntimeException("Prime introuvable"));

        p.setStatut(StatutPrime.VALIDEE);
        return PaieMapper.toDto(p);
    }

    @Transactional
    public PrimeDTO payer(Long id) {
        Prime p = repository.findByIdWithGardien(id)
                .orElseThrow(() -> new RuntimeException("Prime introuvable"));

        p.setStatut(StatutPrime.PAYEE);
        return PaieMapper.toDto(p);
    }

    @Transactional
    public PrimeDTO annuler(Long id) {
        Prime p = repository.findByIdWithGardien(id)
                .orElseThrow(() -> new RuntimeException("Prime introuvable"));

        p.setStatut(StatutPrime.ANNULEE);
        return PaieMapper.toDto(p);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<PrimeDTO> findAll() {
        return repository.findAllWithGardien()
                .stream()
                .map(PaieMapper::toDto)
                .toList();
    }

    @Override
    public PrimeDTO save(PrimeDTO dto) {
        // TODO Auto-generated method stub
        return null;
    }
}
