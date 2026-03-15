package com.king.peace.Web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.king.peace.Dto.PaieDTO;
import com.king.peace.Dto.PaieGenerationMasseDTO;
import com.king.peace.Dto.PaieGenerationMasseRequestDTO;
import com.king.peace.Dto.PaieSuppressionItemDTO;
import com.king.peace.Dto.PaiementDashboardDTO;

import com.king.peace.ImplementServices.PaiementSalaireServiceImpl;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaiementController {

    private final PaiementSalaireServiceImpl service;
 // Générer la paie d'un gardien pour un mois/année
    @PostMapping("/generer")
    public ResponseEntity<PaieDTO> genererPaie(
            @RequestParam Long gardienId,
            @RequestParam LocalDate datePaieDebut,
            @RequestParam LocalDate datePaieFin
    ) {
        return ResponseEntity.ok(service.genererPaie(gardienId, datePaieDebut, datePaieFin));
    }

@PostMapping("/generer-masse")
public ResponseEntity<PaieGenerationMasseDTO> genererPaieMasse(
         @RequestBody PaieGenerationMasseRequestDTO request
) {
    return ResponseEntity.ok(service.genererPaieMasse(request));
}


@PutMapping("/valider-masse")
public ResponseEntity<List<PaieDTO>> validerMasse(@RequestBody List<Long> ids) {
    return ResponseEntity.ok(service.validerMasse(ids));
}

@PutMapping("/payer-masse")
public ResponseEntity<List<PaieDTO>> payerMasse(@RequestBody List<Long> ids) {
    return ResponseEntity.ok(service.payerMasse(ids));
}

//suppression

 @DeleteMapping("/supprimer-masse")
    public ResponseEntity<List<PaieSuppressionItemDTO>> supprimerMasse(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(service.supprimerMasse(ids));
    }


@PutMapping("/annuler-masse")
public ResponseEntity<List<PaieDTO>> annulerMasse(@RequestBody List<Long> ids) {
    return ResponseEntity.ok(service.annulerMasse(ids));
}
    
    // Liste de toutes les paies
    @GetMapping
    public ResponseEntity<List<PaieDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Détail d'une paie
    @GetMapping("/{id}")
    public ResponseEntity<PaieDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Lister les paies d'un mois/année
    @GetMapping("/periode")
    public ResponseEntity<List<PaieDTO>> findByPeriode(
            @RequestParam LocalDate dateDebut,
        @RequestParam LocalDate dateFin
    ) {
        return ResponseEntity.ok(service.findByPeriode(dateDebut, dateFin));
    }

    // Lister les paies d'un gardien
    @GetMapping("/gardien/{gardienId}")
    public ResponseEntity<List<PaieDTO>> findByGardien(@PathVariable Long gardienId) {
        return ResponseEntity.ok(service.findByGardien(gardienId));
    }

    // Marquer une paie comme payée
    @PutMapping("/{id}/payer")
    public ResponseEntity<PaieDTO> payer(@PathVariable Long id) {
        return ResponseEntity.ok(service.payer(id));
    }

    // Annuler une paie
    @PutMapping("/{id}/annuler")
    public ResponseEntity<PaieDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(service.annuler(id));
    }

    // Supprimer une paie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistique")
    public PaiementDashboardDTO getDashboard(
            @RequestParam LocalDate dateDebut,
            @RequestParam LocalDate dateFin
    ) {
        return service.statistique(dateDebut, dateFin);
    }

}
