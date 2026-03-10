package com.king.peace.Entitys;

import com.king.peace.enums.SensLignePaie;
import com.king.peace.enums.TypeLignePaie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paie_lignes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaieLigne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type_ligne", nullable = false)
    private TypeLignePaie typeLigne;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensLignePaie sens;


    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "paie_id", nullable = false)
private Paie paies;
}