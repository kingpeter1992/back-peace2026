package com.king.peace.Web;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.peace.Dto.AvanceSalaireDTO;
import com.king.peace.Dto.GenererPaieRequest;
import com.king.peace.Dto.PaiementSalaireDTO;
import com.king.peace.Dto.PretDTO;
import com.king.peace.Dto.PrimeDTO;
import com.king.peace.Dto.RetenueDTO;
import com.king.peace.Interfaces.AvanceSalaireService;
import com.king.peace.Interfaces.PaiementSalaireService;
import com.king.peace.Interfaces.PretService;
import com.king.peace.Interfaces.PrimeService;
import com.king.peace.Interfaces.RetenueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaiementController {


private final PrimeService servicePaiement;

    @PostMapping
    public PrimeDTO savePaiement(@RequestBody PrimeDTO dto) { return servicePaiement.save(dto); }

    @GetMapping("/fildAllPaiement")
    public List<PrimeDTO> findAllPaiement() { return servicePaiement.findAll(); }

    @DeleteMapping("deletePaiementByid/{id}")
    public void deletePaiementById(@PathVariable Long id) { servicePaiement.delete(id); }


     private final RetenueService serviceRetenues;

    @PostMapping("/saveRetenues")
    public RetenueDTO saveRetenues(@RequestBody RetenueDTO dto) { return serviceRetenues.save(dto); }

    @GetMapping("/retenues")
    public List<RetenueDTO> findAllRetenues() { return serviceRetenues.findAll(); }

    @DeleteMapping("deleteRetenuesById/{id}")
    public void deleteRetenuesById(@PathVariable Long id) { serviceRetenues.delete(id); }
    
     private final AvanceSalaireService serviceAvance;

    @PostMapping("/saveAvance")
    public AvanceSalaireDTO saveAvance(@RequestBody AvanceSalaireDTO dto) { return serviceAvance.save(dto); }

    @PutMapping("/{id}/validerAvance")
    public AvanceSalaireDTO validerAvance(@PathVariable Long id) { return serviceAvance.valider(id); }

    @GetMapping("/findAllAvance")
    public List<AvanceSalaireDTO> findAllAvance() { return serviceAvance.findAll(); }

    @DeleteMapping("/deleteAvanceById/{id}")
    public void deleteAvanceById(@PathVariable Long id) { serviceAvance.delete(id); }


     private final PretService servicePret;

    @PostMapping("/savePret")
    public PretDTO savePret(@RequestBody PretDTO dto) { return servicePret.save(dto); }

    @GetMapping("/findAllPrets")
    public List<PretDTO> findAllPrets() { return servicePret.findAll(); }

    @DeleteMapping("/deletePretById/{id}")
    public void delete(@PathVariable Long id) { servicePret.delete(id); }

      private final PaiementSalaireService servicePaiementSalaire;

    @PostMapping("/genererPaiementSalaire")
    public PaiementSalaireDTO genererPaiementSalaire(@RequestBody GenererPaieRequest request) {
        return servicePaiementSalaire.genererPaie(request);
    }

    @PutMapping("/{id}/validerPaiementSalaire")
    public PaiementSalaireDTO valider(@PathVariable Long id) {
        return servicePaiementSalaire.validerPaie(id);
    }

    @GetMapping("/findAllPaiementSalaire")
    public List<PaiementSalaireDTO> findAllPaiementSalaire() {
        return servicePaiementSalaire.findAll();
    }
}
