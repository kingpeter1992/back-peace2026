package com.king.peace.Web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.king.peace.Dao.TauxJournalierRepository;
import com.king.peace.Dao.dashDto.DashboardGlobalDTO;
import com.king.peace.Dao.dashDto.LabelValueDTO;
import com.king.peace.Dao.dashDto.MonthTotalDTO;
import com.king.peace.Dto.CaisseSessionDto;
import com.king.peace.Dto.CaisseSummaryDTO;
import com.king.peace.Dto.CloturerCaisseDTO;
import com.king.peace.Dto.OperationCaisseDTO;
import com.king.peace.Dto.OuvrirCaisseDTO;
import com.king.peace.Dto.SessionReportDTO;
import com.king.peace.Dto.TransactionCaisseDto;
import com.king.peace.Dto.TxReportDTO;
import com.king.peace.Dto.Response.CaisseSessionResponseDTO;
import com.king.peace.Dto.Response.LettrageContextDto;
import com.king.peace.Dto.Response.LettrerManyRequest;
import com.king.peace.Dto.Response.LettrerManyResult;
import com.king.peace.Dto.Response.TransactionCaisseResponse;
import com.king.peace.Entitys.CaisseSession;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.TauxJournalier;
import com.king.peace.Entitys.TransactionCaisse;
import com.king.peace.ImplementServices.CaisseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/caisse")
@RequiredArgsConstructor
public class CaisseController {

    private final CaisseService caisseService;
    private final TauxJournalierRepository tauxChangeRepository;


    @PostMapping("/ouvrir")
    public ResponseEntity<?> ouvrir(@RequestBody OuvrirCaisseDTO dto, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(caisseService.ouvrirCaisse(dto, username));
    }

    @PostMapping("/cloturer")
    public ResponseEntity<?> cloturer(@RequestBody CloturerCaisseDTO dto,
        Principal principal
    ) {
                String username = principal.getName();
             CaisseSession saved =   caisseService.cloturerCaisse(dto, username);

        return ResponseEntity.ok(toResponseCessionCaisse(saved));
    }


    private CaisseSessionResponseDTO toResponseCessionCaisse(CaisseSession session) {

    return CaisseSessionResponseDTO.builder()
            .id(session.getId())
            .dateJour(session.getDateJour())
            .statut(session.getStatut() != null ? session.getStatut().name() : null)

            .soldeInitialUSD(session.getSoldeInitialUSD())
            .soldeInitialCDF(session.getSoldeInitialCDF())

            .soldeActuelUSD(session.getSoldeActuelUSD())
            .soldeActuelCDF(session.getSoldeActuelCDF())

            .dateOuverture(session.getDateOuverture())
            .dateCloture(session.getDateCloture())

            .openedBy(session.getOpenedBy())
            .closedBy(session.getClosedBy())

            .noteOuverture(session.getNoteOuverture())
            .noteCloture(session.getNoteCloture())           
            .build();
}

  @PostMapping("/operationclient")
public ResponseEntity<?> operationClient(@RequestBody OperationCaisseDTO dto, Principal principal) {
    String username = principal.getName();
    TransactionCaisse saved =  caisseService.encaisserClientPro(dto, username);
    return ResponseEntity.ok(toResponse(saved)); // ou mapper ici
}

@GetMapping("/last")
public ResponseEntity<Double> getLastTaux() {
    TauxJournalier taux = tauxChangeRepository
        .findTopByActifTrueOrderByCreatedAtDesc()
        .orElseThrow(() -> new RuntimeException("Aucun taux trouvé"));

    return ResponseEntity.ok(taux.getTaux());
}
@PostMapping("/operationgardien")
public ResponseEntity<?> operationGardien(@RequestBody OperationCaisseDTO dto, Principal principal) {
    String username = principal.getName();
    TransactionCaisse saved = caisseService.enregistrerOperationGardien(dto, username);
    return ResponseEntity.ok(toResponse(saved)); 
}

@PostMapping("/operationautre")
public ResponseEntity<?> operationAutre(@RequestBody OperationCaisseDTO dto, Principal principal) {
    String username = principal.getName();
TransactionCaisse saved = caisseService.enregistrerOperationAutre(dto, username);
    return ResponseEntity.ok(toResponse(saved)); // ou mapper ici
}
    @GetMapping("/historique")
    public ResponseEntity<?> historique() {
        return ResponseEntity.ok(caisseService.historiqueDuJour());
    }

    @GetMapping("/session/ouverte")
public ResponseEntity<CaisseSessionDto> sessionOuverte() {
    return ResponseEntity.ok(caisseService.getSessionOuverteDuJour());
}

@GetMapping("/historique/jour")
public ResponseEntity<List<TransactionCaisseDto>> historiqueDuJour() {
    return ResponseEntity.ok(caisseService.historiqueDuJour());
}

@GetMapping("/report")
public ResponseEntity<?> rapportCaisse(
        @RequestParam LocalDate dateFrom,
        @RequestParam LocalDate dateTo
) {

    CaisseSummaryDTO summary = caisseService.buildSummary(dateFrom, dateTo);

     List<TxReportDTO> operations = caisseService.findByDateRange(
            dateFrom.atStartOfDay(),
            dateTo.plusDays(1).atStartOfDay()
    );


    Map<String, Object> response = new HashMap<>();
    response.put("summary", summary);
    response.put("operations", operations);
    

    return ResponseEntity.ok(response);
}



private TransactionCaisseResponse toResponse(TransactionCaisse t) {
    return new TransactionCaisseResponse(
            t.getId(),
            t.getReference(),
            t.getMontant(),
            t.getDevise(),
            t.getType(),
            t.getCategory(),
            t.getDateTransaction(),
            t.getSoldeAvant(),
            t.getSoldeApres(),
            t.getSens(),
            t.getClient() != null ? t.getClient().getId() : null,
            t.getGardien() != null ? t.getGardien().getId() : null,
            t.getSession() != null ? t.getSession().getId() : null
    );
}

@GetMapping("/lettrage/context")
public LettrageContextDto context(@RequestParam Long clientId, @RequestParam Devise devise) {
  return caisseService.getLettrageContext(clientId, devise);
}

@PostMapping("/lettrage/lettrer-many")
public LettrerManyResult lettrerMany(@RequestBody LettrerManyRequest req, Principal principal) {
  return caisseService.lettrerMany(req, principal.getName());
}


}
