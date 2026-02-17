package com.king.peace.Dto;

import java.time.LocalDate;
import java.util.Base64;

import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.GardienPhoto;
import com.king.peace.Entitys.StatutGardien;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GardienDto {
      private Long id;
    private String nom;
    private String prenom;
    private String telephone1;
    private String telephone2;
    private String fonction;
    private double salaire;
    private String adresse;
    private String genre;
    private double salaireBase;
//    private double bonus;
    private StatutGardien Statut;
    private LocalDate dateEmbauche;
    private String email;
    private LocalDate dateNaissance;
    private LocalDate createdAt;
    private String devise;
    private String photo;
        private String photoBase64; // photo convertie en Base64 pour l'aperçu


  public GardienDto(
            Long id,
            String nom,
            String prenom,
            String fonction,
            double salaireBase,
            StatutGardien statut
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.salaireBase = salaireBase;
      //  this.statut = statut;
    }

    public GardienDto() {}

     public GardienDto(Gardien g, GardienPhoto photo) {
        this.id = g.getId();
        this.nom = g.getNom();
        this.prenom = g.getPrenom();
        this.fonction = g.getFonction();
        this.salaireBase = g.getSalaireBase();
       // this.statut = g.getStatut();

        if (photo != null && photo.getPhoto() != null) {
            this.photoBase64 = Base64.getEncoder().encodeToString(photo.getPhoto());
        }
    }

}
