package com.king.peace.Entitys;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
@Setter
@Getter
public class Pointage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalDate datesortie;
    private LocalTime heureEntree;
    private LocalTime heureSortie;
    @ManyToOne private Gardien gardien;

     @Enumerated(EnumType.STRING)
    private StatutPointage statut;
}
