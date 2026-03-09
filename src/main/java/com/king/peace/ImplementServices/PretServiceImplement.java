package com.king.peace.ImplementServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.PretRepository;
import com.king.peace.Dto.PretDTO;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Pret;
import com.king.peace.Utiltys.PaieMapper;
import com.king.peace.enums.StatutPret;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PretServiceImplement {
    
 private final PretRepository repository;
 private final GardienRepository employeRepository;


    public PretDTO save(PretDTO dto) {
//        System.out.println("Devise reçue = " + dto.getDevise());
        Gardien employe = employeRepository.findById(dto.getGardienId()).orElseThrow();

        //check  s'il  à déjà  un  pret en cours
        List<Pret> prets = repository.findByGardienIdAndStatut(employe.getId(), StatutPret.EN_COURS);
        if (!prets.isEmpty()) {
            throw new RuntimeException("Le gardien a déjà un prêt en cours");
        }



        Pret pret = Pret.builder()
                .id(dto.getId())
                .gardien(employe)
                .montantTotal(dto.getMontantTotal())
                .montantRestant(dto.getMontantRestant() == 0 ? dto.getMontantTotal() : dto.getMontantRestant())
                .nombreMois(dto.getNombreMois())
                .mensualite(dto.getMensualite())
                .dateDebut(dto.getDateDebut())
                .statut(dto.getStatut() == null ? StatutPret.EN_COURS : dto.getStatut())
                .motif(dto.getMotif())
                .devise(dto.getDevise())
                .build();
        return PaieMapper.toDto(repository.save(pret));
    }


   @Transactional
public List<PretDTO> findAll() {
    return repository.findAllWithGardien()
            .stream()
            .map(PaieMapper::toDto)
            .toList();
}

    public void delete(Long id) {
        repository.deleteById(id);
    }


    public PretDTO getById(Long id) {
        Pret pret = repository.findById(id).orElseThrow();
        return PaieMapper.toDto(pret);
    }


    public PretDTO saveUpdate(Long id, PretDTO updaDto) {
        Pret pret = repository.findById(id).orElseThrow();

        pret.setMontantTotal(updaDto.getMontantTotal());
        pret.setMontantRestant(updaDto.getMontantRestant());
        pret.setNombreMois(updaDto.getNombreMois());
        pret.setMensualite(updaDto.getMensualite());
        pret.setDateDebut(updaDto.getDateDebut());
        pret.setGardien(pret.getGardien());
        pret.setStatut(updaDto.getStatut());
        pret.setMotif(updaDto.getMotif());
        pret.setDevise(updaDto.getDevise());
        repository.save(pret);
        return updaDto;
    }

   @Transactional
public PretDTO cloturerPret(Long id) {

    Pret pret = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prêt introuvable"));


            if (pret.getMontantRestant() > 0) {
    throw new RuntimeException("Impossible de clôturer : montant restant non nul");
}

    pret.setStatut(StatutPret.TERMINE);
    pret.setMontantRestant(0.0);

    repository.save(pret);

    return PaieMapper.toDto(pret);
}
}
