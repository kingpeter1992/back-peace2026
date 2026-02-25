package com.king.peace.ImplementServices;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dto.ContratsdTO;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratService {
     private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;

    public ContratsdTO createContrat(ContratsdTO contrat, Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        
        Contrats contrats = new Contrats();
        contrats.setDateDebut(contrat.getDateDebut());
        contrats.setDateFin(contrat.getDateFin());
        contrats.setTypeService(contrat.getTypeService());
        contrats.setNombreGardiens(contrat.getNombreGardiens());
        contrats.setMontantParGardien(contrat.getMontantParGardien());
        contrats.setNombreJoursMensuel(contrat.getNombreJoursMensuel());
        contrats.setDevise(contrat.getDevise());
        contrats.setTypePaiement(contrat.getTypePaiement());
        contrats.setClient(client);
        contrats.setRefContrats(contrat.getRefContrats());
        contrats.setStatut(contrat.getStatut());
        contrats.setDescription(contrat.getDescription());
        contrats.setDateEmission(LocalDate.now());
        contrats.setZone(contrat.getZone());
        contrats.setActiviteClient(contrat.getActiviteClient());
        contrats.setActive(true);
        contrats.setDateDebutFacturation(contrat.getDateDebutFacturation());
        
contratRepository.save(contrats);
        return contrat;
    }

   public List<ContratsdTO> getAllContrats() {

    return contratRepository.findAll()
            .stream()
            .map(c -> {
                ContratsdTO dto = new ContratsdTO();

                dto.setId(c.getId());
                dto.setRefContrats(c.getRefContrats());
                dto.setZone(c.getZone());
                dto.setTypeService(c.getTypeService());
                dto.setNombreGardiens(c.getNombreGardiens());
                dto.setMontant(c.getMontant());
                dto.setStatut(c.getStatut());
                dto.setClientId(c.getClient().getId());
                dto.setClientNom(c.getClient().getNom());
                dto.setDateDebut(c.getDateDebut());
                dto.setDateFin(c.getDateFin());
                dto.setNombreJoursMensuel(c.getNombreJoursMensuel());
                dto.setActiviteClient(c.getActiviteClient());
                dto.setDevise(c.getDevise());
                dto.setTypePaiement(c.getTypePaiement());
                dto.setDateEmission(c.getDateEmission());
                dto.setDateDebutFacturation(c.getDateDebutFacturation());
                dto.setDescription(c.getDescription());
                dto.setActive(c.isActive());
                dto.setMontantParGardien(c.getMontantParGardien());
                dto.setFrequence(c.getFrequence());

                return dto;
            })
            .toList();
}


    public Contrats getContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));
    }

 // Modification du contrat (nombre de gardiens, montant, type paiement)
public ContratsdTO updateContrat(Long contratId, ContratsdTO updated) {
    Contrats contrat = contratRepository.findById(contratId)
            .orElseThrow(() -> new RuntimeException("Contrat introuvable"));
        System.out.println("status contrat " + contrat.getStatut());

    // Mise à jour des champs existants
    contrat.setDateDebut(updated.getDateDebut());
    contrat.setDateFin(updated.getDateFin());
    contrat.setTypeService(updated.getTypeService());
    contrat.setNombreGardiens(updated.getNombreGardiens());
    contrat.setMontantParGardien(updated.getMontantParGardien());
    contrat.setNombreJoursMensuel(updated.getNombreJoursMensuel());
    contrat.setDevise(updated.getDevise());
    contrat.setActiviteClient(updated.getActiviteClient());
    contrat.setZone(updated.getZone());
    contrat.setStatut(updated.getStatut());
    contrat.setDescription(updated.getDescription());
    contrat.setDateEmission(LocalDate.now());
    contrat.setMontant(updated.getMontant());
    contrat.setFrequence(updated.getFrequence());
    contrat.setDateDebutFacturation(updated.getDateDebutFacturation());
    contrat.setActive(updated.isActive());
    contrat.setRefContrats(updated.getRefContrats());
    contrat.setClient(contrat.getClient());
    contrat.setTypePaiement(updated.getTypePaiement());

    // Sauvegarde du contrat mis à jour
    contratRepository.save(contrat);

    return updated; // ou éventuellement convertir l'objet mis à jour depuis contrat
}

public List<ContratsdTO> findContratsActifsEnCours(LocalDate today) {
        List<Contrats> contrats = contratRepository
                .findByActiveTrueAndDateDebutBeforeAndDateFinAfter(today, today);

        return contrats.stream()
                .map(c -> {
                    var client = clientRepository.findById(c.getClient().getId()).orElse(null);
                    return ContratsdTO.builder()
                            .id(c.getId())
                            .refContrats(c.getRefContrats())
                            .zone(c.getZone())
                            .typeService(c.getTypeService())
                            .nombreGardiens(c.getNombreGardiens())
                            .montant(c.getMontant())
                            .statut(c.getStatut())
                            .clientId(c.getClient().getId())
                            .clientNom(client != null ? client.getNom() : null)
                            .dateDebut(c.getDateDebut())
                            .dateFin(c.getDateFin())
                            .nombreJoursMensuel(c.getNombreJoursMensuel())
                            .activiteClient(c.getActiviteClient())
                            .devise(c.getDevise())
                            .typePaiement(c.getTypePaiement())
                            .dateEmission(c.getDateEmission())
                            .dateDebutFacturation(c.getDateDebutFacturation())
                            .description(c.getDescription())
                            .active(c.isActive())
                            .montantParGardien(c.getMontantParGardien())
                            .frequence(c.getFrequence())
                            .build();
                })
                .collect(Collectors.toList());
    }
}


