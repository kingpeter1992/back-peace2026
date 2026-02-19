package com.king.peace.Web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dto.FactureDTO;
import com.king.peace.Dto.UpdateFactureRequest;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutFacture;
import com.king.peace.ImplementServices.FacturationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FacturationService factureService;
    private final FactureRepository factureRepository;

    @GetMapping("/all")
    public List<FactureDTO> getAllFactures() {
        return factureRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactureDTO> updateFacture(@PathVariable Long id,
                                                    @RequestBody FactureDTO dto) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        facture.setMontantTotal(dto.getMontantTotal());
        facture.setDescription(dto.getDescription());
        facture.setStatut(StatutFacture.valueOf(dto.getStatut()));


        factureRepository.save(facture);
        return ResponseEntity.ok(toDto(facture));
    }

    private FactureDTO toDto(Facture facture) {
        return FactureDTO.builder()
                .id(facture.getId())
                .dateEmission(facture.getDateEmission())
                .montantTotal(facture.getMontantTotal())
                .statut(facture.getStatut().name())
                .description(facture.getDescription())
                .refFacture(facture.getRefFacture())
                .nombreGardiens(facture.getNombreGardiens())
                .montantParGardien(facture.getMontantParGardien())
                .nombreJours(facture.getNombreJours())
                .devise(facture.getDevise().name())
                .clientId(facture.getClient().getId())
                .contratId(facture.getContrats().getId())
                .build();
    }


    @PutMapping("/modify/{id}")
public ResponseEntity<UpdateFactureRequest> modifyFacture(@PathVariable Long id, 
                                                @RequestBody UpdateFactureRequest dto) {
    UpdateFactureRequest updated = factureService.updateFacture(id, dto);
    return ResponseEntity.ok(updated);
}

@PostMapping("/manual")
public ResponseEntity<FactureDTO> createManualFacture(@RequestBody FactureDTO dto) {
    FactureDTO created = factureService.createManualFacture(dto);
    return ResponseEntity.ok(created);
}

@PostMapping("/avoir/{id}")
public ResponseEntity<FactureDTO> createAvoir(@PathVariable Long id,
                                              @RequestBody FactureDTO dto) {
    FactureDTO avoir = factureService.createAvoir(id, dto);
    return ResponseEntity.ok(avoir);
}


@GetMapping("/stats/monthly")
public List<Map<String, Object>> monthly(
    @RequestParam LocalDate dateFrom,
    @RequestParam LocalDate dateTo,
    @RequestParam(required = false) Long clientId
) {
  return factureService.monthlyStats(dateFrom, dateTo, clientId);
}

}
