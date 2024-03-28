package com.fdm.velocitytrade.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdm.velocitytrade.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

}
