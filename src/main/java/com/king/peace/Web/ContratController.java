package com.king.peace.Web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.king.peace.Dto.AffectationDto;
import com.king.peace.Dto.ContratsdTO;
import com.king.peace.Entitys.Affectation;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Gardien;
import com.king.peace.ImplementServices.AffectationService;
import com.king.peace.ImplementServices.ContratService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContratController {

    private final ContratService contratService;
    private final AffectationService affectationService;

    // ===========================
    // CRUD CONTRAT
    // ===========================

    // Créer un contrat
    @PostMapping("/client/{clientId}")
    public ResponseEntity<ContratsdTO> createContrat(@PathVariable Long clientId,
                                                 @RequestBody ContratsdTO contrat) {
        ContratsdTO saved = contratService.createContrat(contrat, clientId);
        return ResponseEntity.ok(saved);
    }

    // Modifier un contrat existant
  @PutMapping("/{contratId}")
public ResponseEntity<ContratsdTO> updateContrat(@PathVariable Long contratId,
                                                 @RequestBody ContratsdTO contratDto) {
    ContratsdTO updated = contratService.updateContrat(contratId, contratDto);
    return ResponseEntity.ok(updated);
}


    // Lister tous les contrats
    @GetMapping
    public ResponseEntity<List<ContratsdTO>> getAllContrats() {
        return ResponseEntity.ok(contratService.getAllContrats());
    }

    // Récupérer un contrat par ID
    @GetMapping("/{contratId}")
    public ResponseEntity<Contrats> getContrat(@PathVariable Long contratId) {
        return ResponseEntity.ok(contratService.getContrat(contratId));
    }

    // ===========================
    // AFFECTATION DES GARDIENS
    // ===========================

     @GetMapping("/actifs-en-cours")
    public List<ContratsdTO> getActiveContratsEnCours() {
        return contratService.findContratsActifsEnCours(LocalDate.now());
    }
    
    
}
