package com.fdm.velocitytrade.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdm.velocitytrade.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
