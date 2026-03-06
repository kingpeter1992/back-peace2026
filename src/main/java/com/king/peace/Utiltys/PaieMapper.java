package com.king.peace.Utiltys;

import com.king.peace.Dto.AvanceSalaireDTO;
import com.king.peace.Dto.GardienDto;
import com.king.peace.Dto.GardienDtos;
import com.king.peace.Dto.PaiementSalaireDTO;
import com.king.peace.Dto.PretDTO;
import com.king.peace.Dto.PrimeDTO;
import com.king.peace.Dto.RetenueDTO;
import com.king.peace.Entitys.AvanceSalaire;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.PaiementSalaire;
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
                .fonction(e.getFonction())
                .salaireBase(e.getSalaireBase())
                .statut(e.getStatut())
                .build();
    }
 public static PrimeDTO toDto(Prime p) {
        return PrimeDTO.builder()
                .id(p.getId())
                .employeId(p.getGardien().getId())
                .employeNom(p.getGardien().getNom())
                .typePrime(p.getTypePrime())
                .libelle(p.getLibelle())
                .montant(p.getMontant())
                .datePrime(p.getDatePrime())
                .observation(p.getObservation())
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
                .employeId(a.getGardien().getId())
                .employeNom(a.getGardien().getNom() + " " + a.getGardien().getPrenom())
                .montant(a.getMontant())
                .dateAvance(a.getDateAvance())
                .statut(a.getStatut())
                .observation(a.getObservation())
                .build();
    }

    public static PretDTO toDto(Pret p) {
        return PretDTO.builder()
                .id(p.getId())
                .employeId(p.getGardien().getId())
                .employeNom(p.getGardien().getNom() + " " + p.getGardien().getPrenom())
                .montantTotal(p.getMontantTotal())
                .montantRestant(p.getMontantRestant())
                .nombreMois(p.getNombreMois())
                .mensualite(p.getMensualite())
                .dateDebut(p.getDateDebut())
                .statut(p.getStatut())
                .motif(p.getMotif())
                .build();
    }

    public static PaiementSalaireDTO toDto(PaiementSalaire p) {
        return PaiementSalaireDTO.builder()
                .id(p.getId())
                .employeId(p.getGardien().getId())
                .employeNom(p.getGardien().getNom() + " " + p.getGardien().getPrenom())
                .mois(p.getMois())
                .annee(p.getAnnee())
                .salaireBase(p.getSalaireBase())
                .totalPrimes(p.getTotalPrimes())
                .totalRetenues(p.getTotalRetenues())
                .totalAvances(p.getTotalAvances())
                .totalRemboursementPret(p.getTotalRemboursementPret())
                .salaireBrut(p.getSalaireBrut())
                .salaireNet(p.getSalaireNet())
                .datePaiement(p.getDatePaiement())
                .statut(p.getStatut())
                .build();
    }
}
