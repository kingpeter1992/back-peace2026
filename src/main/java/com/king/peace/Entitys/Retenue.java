package com.king.peace.Entitys;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.king.peace.enums.TypeRetenue;

@Entity
@Table(name = "retenues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Retenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardien_id", nullable = false)
    private Gardien gardien;

    @Enumerated(EnumType.STRING)
    private TypeRetenue typeRetenue;

    @Column(nullable = false)
    private String libelle;

    private double montant;

    @Column(nullable = false)
    private LocalDate dateRetenue;

    private String motif;

        @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

}