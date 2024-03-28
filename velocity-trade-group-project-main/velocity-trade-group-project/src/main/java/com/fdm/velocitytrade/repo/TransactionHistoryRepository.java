package com.fdm.velocitytrade.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdm.velocitytrade.model.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    
    @Query("SELECT th FROM TransactionHistory th JOIN FETCH th.user u WHERE u.id = :userId")
    List<TransactionHistory> findByUserId(@Param("userId") Long userId);

}
