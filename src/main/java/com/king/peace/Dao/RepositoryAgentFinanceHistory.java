package com.king.peace.Dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.AgentFinanceHistory;

public interface RepositoryAgentFinanceHistory extends JpaRepository<AgentFinanceHistory,Long> {

    List<AgentFinanceHistory> findByGardien_Id(Long gardienId);

    List<AgentFinanceHistory> findByGardien_IdAndDateBetween(
        Long gardienId,
        LocalDateTime dateFrom,
        LocalDateTime dateTo
);
    
}
