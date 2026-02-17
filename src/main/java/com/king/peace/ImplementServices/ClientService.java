package com.king.peace.ImplementServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.FactureRepository;
import com.king.peace.Dto.ClientDto;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Facture;
import com.king.peace.Entitys.StatutGardien;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

     private final ClientRepository clientRepository;
    private final ContratRepository contratRepository;
    private final FactureRepository factureRepository;

    // Création d'un client
    public Client creerClient(Client client) {
    client.setNom(client.getNom().toUpperCase());
    client.setAdresse(client.getAdresse().toUpperCase());
    client.setContact(client.getContact().toUpperCase());
    client.setContact2(client.getContact2().toUpperCase());
    client.setEmail(client.getEmail().toUpperCase());

        return clientRepository.save(client);
    }

    // Signer un contrat pour un client
    public Contrats signerContrat(Long clientId, Contrats contrat) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        contrat.setClient(client);
        contrat.setStatut("ACTIF");
        return contratRepository.save(contrat);
    }

    // Historique des factures d'un client
    public List<Facture> getFacturesClient(Long clientId) {
        return factureRepository.findByClientId(clientId);
    }

// Mettre à jour un client
  public ClientDto updateClient(Long id, Client client) {
    Client existing = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client introuvable"));

    existing.setNom(client.getNom().toUpperCase());
    existing.setAdresse(client.getAdresse().toUpperCase());
    existing.setContact(client.getContact().toUpperCase());
    existing.setContact2(client.getContact2().toUpperCase());
    existing.setEmail(client.getEmail().toUpperCase());
    existing.setTypeClient(client.getTypeClient().toUpperCase());

    Client saved = clientRepository.save(existing);

    // Retourne le DTO pour éviter le lazy loading
    return new ClientDto(
        saved.getId(),
        saved.getNom(),
        saved.getAdresse(),
        saved.getContact(),
        saved.getContact2(),
        saved.getEmail(),
        saved.getTypeClient()
    );
}


    // Supprimer un client
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        clientRepository.delete(client);
    }

    // Liste de tous les clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Obtenir un client par id
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
    }

    // Obtenir tous les contrats d'un client
    public List<Contrats> getContratsByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return contratRepository.findByClient(client);
    }

    // Obtenir toutes les factures d'un client
    public List<Facture> getFacturesByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return factureRepository.findByClient(client);
    }

}
