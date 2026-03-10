package com.king.peace.Utiltys;

import java.util.List;

import com.king.peace.Dto.AvanceSalaireDTO;
import com.king.peace.Dto.GardienDtos;
import com.king.peace.Dto.PaieDTO;
import com.king.peace.Dto.PaieLigneDTO;
import com.king.peace.Dto.PretDTO;
import com.king.peace.Dto.PrimeDTO;
import com.king.peace.Dto.RetenueDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.Paie;
import com.king.peace.Entitys.PaieLigne;
import com.king.peace.Entitys.Pret;
import com.king.peace.Entitys.Prime;
import com.king.peace.Entitys.Retenue;

public class PaieMapper {

     public static GardienDtos toDto(Gardien e) {
        return GardienDtos.builder()
                .id(e.getId())
//                .matricule(e.getMatricule())
                .nom(e.getNom())
                .prenom(e.getPrenom())
                .salaireBase(e.getSalaireBase())
                .statut(e.getStatut())
                .departement(e.getDepartement())
                .site(e.getSite())
                .fonction(e.getFonction())
                .build();
    }


     public static RetenueDTO toDto(Retenue r) {
        return RetenueDTO.builder()
                .id(r.getId())
                .employeId(r.getGardien().getId())
                .employeNom(r.getGardien().getNom() + " " + r.getGardien().getPrenom())
                .typeRetenue(r.getTypeRetenue())
                .libelle(r.getLibelle())
                .montant(r.getMontant())
                .dateRetenue(r.getDateRetenue())
                .motif(r.getMotif())
                .build();
    }




    public static AvanceSalaireDTO toDto(AvanceSalaire a) {
        return AvanceSalaireDTO.builder()
                .id(a.getId())
                .gardienId(a.getGardien().getId())
                .gardienNom(a.getGardien().getNom() + " " + a.getGardien().getPrenom())
                .montant(a.getMontant())
                .dateAvance(a.getDateAvance())
                .statut(a.getStatut())
                .observation(a.getObservation())
                .motif(a.getMotif())
                .devise(a.getDevise())
                .anneeConcerne(a.getAnneeConcerne())
                .moisConcerne(a.getMoisConcerne())
                .build();
    }
    public static PretDTO toDto(Pret p) {
        return PretDTO.builder()
                .id(p.getId())
                .gardienId(p.getGardien().getId())
                .gardienNom(p.getGardien().getNom() + " " + p.getGardien().getPrenom())
                .montantTotal(p.getMontantTotal())
                .montantRestant(p.getMontantRestant())
                .nombreMois(p.getNombreMois())
                .mensualite(p.getMensualite())
                .dateDebut(p.getDateDebut())
                .statut(p.getStatut())
                .motif(p.getMotif())
                .devise(p.getDevise())
                .build();
    }

 public static PrimeDTO toDto(Prime p) {
        return PrimeDTO.builder()
                .id(p.getId())
                .gardienId(p.getGardien() != null ? p.getGardien().getId() : null)
                .gardienNom(
                        p.getGardien() != null
                                ? p.getGardien().getNom() + " " + p.getGardien().getPrenom()
                                : null
                )
                .montant(p.getMontant())
                .devise(p.getDevise())
                .datePrime(p.getDatePrime())
                .typePrime(p.getTypePrime())
                .motif(p.getMotif())
                .statut(p.getStatut())
                .moisConcerne(p.getMoisConcerne())
                .anneeConcerne(p.getAnneeConcerne())
                .observation(p.getObservation())
                .build();
    }


     public static PaieDTO toDtoComplet(Paie paie) {

        List<PaieLigneDTO> lignes = paie.getPaieLignes() == null
                ? List.of()
                : paie.getPaieLignes()
                      .stream()
                      .map(PaieMapper::toDtoLigne)
                      .toList();

        return PaieDTO.builder()
                .id(paie.getId())
                .gardienId(paie.getGardien() != null ? paie.getGardien().getId() : null)
                .gardienNom(
                        paie.getGardien() != null
                                ? paie.getGardien().getNom() + " " + paie.getGardien().getPrenom()
                                : null
                )
                .datePaieDebut(paie.getDatePaieDebut())
                .datePaieFin(paie.getDatePaieFin())
                .datePaie(paie.getDatePaie())
                .salaireBase(paie.getSalaireBase())
                .devise(paie.getDevise())
                .totalPrimes(paie.getTotalPrimes())
                .totalAvances(paie.getTotalAvances())
                .totalPrets(paie.getTotalPrets())
                .autresRetenues(paie.getAutresRetenues())
                .netAPayer(paie.getNetAPayer())
                .statut(paie.getStatut())
                .observation(paie.getObservation())
                .lignes(lignes)
                .build();
    }

    private static PaieLigneDTO toDtoLigne(PaieLigne ligne) {
        return PaieLigneDTO.builder()
                .id(ligne.getId())
                .typeLigne(ligne.getTypeLigne())
                .referenceId(ligne.getReferenceId())
                .libelle(ligne.getLibelle())
                .montant(ligne.getMontant())
                .sens(ligne.getSens())
                .build();
    }
}
