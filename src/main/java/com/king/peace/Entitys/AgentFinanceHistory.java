package com.king.peace.Entitys;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.aspectj.weaver.loadtime.Agent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agent_finance_history")
@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
public class AgentFinanceHistory {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // SALAIRE, AVANCE, PRET
    private Double montant;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "gardien_id")
    private Gardien gardien;

       @OneToOne
    @JoinColumn(name = "transaction_caisse_id")
    private TransactionCaisse transactionCaisse;
}
