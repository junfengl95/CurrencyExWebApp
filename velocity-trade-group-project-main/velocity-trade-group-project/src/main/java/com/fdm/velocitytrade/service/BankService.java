package com.fdm.velocitytrade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.exceptions.BankNotFoundException;
import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.repo.BankRepository;

@Service
public class BankService {

	@Autowired
	BankRepository bankRepo;
	
	public Bank updateBankname(Long id, String name) {
		Bank b = bankRepo.findById(id).orElseThrow(() -> new BankNotFoundException("Bank with id " + id + " not found"));
		
		b.setBankName(name);
		
		return bankRepo.save(b);
	}
	
	public Bank updateAccountNumber(Long id, String acc) {
		Bank b = bankRepo.findById(id).orElseThrow(() -> new BankNotFoundException("Bank with id " + id + " not found"));
		
		b.setAccountNumber(acc);
		
		return bankRepo.save(b);
	}
	
	public Bank findBankById(Long id) {
		Bank b = bankRepo.findById(id).orElseThrow(() -> new BankNotFoundException("Bank with id " + id + " not found"));
		
		return b;
	}
}
