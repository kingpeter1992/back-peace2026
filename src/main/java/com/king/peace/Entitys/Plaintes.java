package com.king.peace.Entitys;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Plaintes {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate datePlaite;
    private String description;
    private String response;
    private LocalDate dateRespnse;
    private String statut;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "gardien_id") // la colonne dans la table
    private Gardien gardien; // ✅ c’est CE nom que Spring va utiliser

}
