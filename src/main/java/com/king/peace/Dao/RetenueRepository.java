package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.Retenue;

import java.time.LocalDate;
import java.util.List;

public interface RetenueRepository extends JpaRepository<Retenue, Long> {
    List<Retenue> findByGardienIdAndDateRetenueBetween(Long gardienId, LocalDate debut, LocalDate fin);


    @Query("""
        select coalesce(sum(r.montant),0)
        from Retenue r
        where r.devise = :devise
        and r.dateRetenue between :dateDebut and :dateFin
    """)
    double sumMontantByDeviseAndPeriod(Devise devise, LocalDate dateDebut, LocalDate dateFin);
}
