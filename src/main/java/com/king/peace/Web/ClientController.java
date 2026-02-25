package com.king.peace.Web;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.king.peace.Dto.ClientDetailsDto;
import com.king.peace.Dto.ClientDto;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Facture;
import com.king.peace.ImplementServices.ClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;


    // Créer un nouveau client
    @PostMapping("/create")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = clientService.creerClient(client);
        return ResponseEntity.ok(savedClient);
    }

    // Modifier un client existant
  @PutMapping("/update/{id}")
public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody Client client) {
    ClientDto updatedClient = clientService.updateClient(id, client);
    return ResponseEntity.ok(updatedClient);
}
    // Supprimer un client
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client supprimé avec succès");
    }

 @GetMapping("/all")
public ResponseEntity<List<ClientDto>> getAllClients() {
    List<Client> clients = clientService.getAllClients();
    List<ClientDto> dtos = clients.stream()
        .map(c -> new ClientDto(c.getId(), c.getNom(), c.getAdresse(),
                               c.getContact(), c.getContact2(), c.getEmail(),
                               c.getTypeClient(),c.isActif()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

    // Consulter un client par id
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    // Consulter tous les contrats d’un client
    @GetMapping("/{id}/contrats")
    public ResponseEntity<List<Contrats>> getContratsByClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getContratsByClient(id));
    }

    // Consulter toutes les factures d’un client
    @GetMapping("/{id}/factures")
    public ResponseEntity<List<Facture>> getFacturesByClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getFacturesClient(id));
    }

@GetMapping("/{clientId}/details")
public ResponseEntity<ClientDetailsDto> details(@PathVariable Long clientId,
        @RequestParam LocalDate dateFrom,
        @RequestParam LocalDate dateTo
) {

    ClientDetailsDto dto = clientService.getClientDetails(clientId,
        dateFrom,dateTo
    );

    return ResponseEntity.ok(dto);
}
}
