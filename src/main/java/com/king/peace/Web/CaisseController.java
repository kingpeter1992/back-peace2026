package com.king.peace.Web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.king.peace.Dto.DecaissementAgentDTO;
import com.king.peace.Dto.DepenseDTO;
import com.king.peace.Dto.EncaissementAgentDTO;
import com.king.peace.Dto.EncaissementClientDTO;
import com.king.peace.Dto.RemboursementClientDTO;
import com.king.peace.Dto.SoldeCaisseDTO;
import com.king.peace.Dto.TransactionCaisseDto;
import com.king.peace.ImplementServices.CaisseService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/caisse")
@RequiredArgsConstructor
public class CaisseController {

    private final CaisseService caisseService;

    // 🔹 Solde CDF
    @GetMapping("/solde/cdf")
    public ResponseEntity<SoldeCaisseDTO> getSoldeCdf() {
        SoldeCaisseDTO dto = caisseService.getSoldeCaisseCdf();
        return ResponseEntity.ok(dto);
    }

    // 🔹 Solde USD
    @GetMapping("/solde/usd")
    public ResponseEntity<SoldeCaisseDTO> getSoldeUsd() {
        SoldeCaisseDTO dto = caisseService.getSoldeCaisseUsd();
        return ResponseEntity.ok(dto);
    }

    // 🔹 Historique complet de la caisse
    @GetMapping("/historique")
    public ResponseEntity<List<TransactionCaisseDto>> getHistorique() {
        List<TransactionCaisseDto> historique = caisseService.historiqueCaisse();
        return ResponseEntity.ok(historique);
    }

    // 🔹 Encaissement client
    @PostMapping("/encaissement-client")
    public ResponseEntity<TransactionCaisseDto> encaissementClient(
            @RequestBody EncaissementClientDTO dto,
            @RequestParam Long userId
    ) {
        TransactionCaisseDto result = caisseService.encaissementClient(dto, userId);
        return ResponseEntity.ok(result);
    }

    // 🔹 Remboursement client
    @PostMapping("/rembourser-client")
    public ResponseEntity<TransactionCaisseDto> rembourserClient(
            @RequestBody RemboursementClientDTO dto,
            @RequestParam Long userId
    ) {
        TransactionCaisseDto result = caisseService.rembourserClient(dto, userId);
        return ResponseEntity.ok(result);
    }

    // 🔹 Dépense générale
    @PostMapping("/depense")
    public ResponseEntity<TransactionCaisseDto> depense(
            @RequestBody DepenseDTO dto,
            @RequestParam Long userId
    ) {
        TransactionCaisseDto result = caisseService.depense(dto, userId);
        return ResponseEntity.ok(result);
    }

    // 🔹 Décaissement agent (Avance, Prêt, Paiement)
    @PostMapping("/decaissement-agent")
    public ResponseEntity<TransactionCaisseDto> decaissementAgent(
            @RequestBody DecaissementAgentDTO dto,
            @RequestParam Long userId
    ) {
        TransactionCaisseDto result = caisseService.decaissementAgent(dto, userId);
        return ResponseEntity.ok(result);
    }

    // 🔹 Encaissement retour agent à la caisse
    @PostMapping("/encaissement-agent")
    public ResponseEntity<TransactionCaisseDto> encaissementAgent(
            @RequestBody EncaissementAgentDTO dto,
            @RequestParam Long userId
    ) {
        TransactionCaisseDto result = caisseService.encaissementAgent(dto, userId);
        return ResponseEntity.ok(result);
    }

}
