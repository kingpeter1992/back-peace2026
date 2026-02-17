package com.king.peace.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.peace.Entitys.CustomerFinanceHistory;

public interface RepositoryCustomerFinanceHistory extends JpaRepository<CustomerFinanceHistory, Long> {
    
}
