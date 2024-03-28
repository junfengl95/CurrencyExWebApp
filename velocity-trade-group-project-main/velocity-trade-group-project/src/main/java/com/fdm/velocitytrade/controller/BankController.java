package com.fdm.velocitytrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.service.BankService;

@RestController
@RequestMapping("banks")
public class BankController {
	@Autowired
	private BankService bankSev;
	
	@PutMapping("/{bankId}/changeBankname/{name}")
	public Bank updateBankName(@PathVariable("bankId") Long id, @PathVariable("name") String bankName) {
		return bankSev.updateBankname(id, bankName);
	}
	
	
	@PutMapping("/{bankId}/changeAccountNumber/{accNum}")
	public Bank updateAccountNumber(@PathVariable("bankId") Long id, @PathVariable("accNum") String acc) {
		return bankSev.updateAccountNumber(id, acc);
	}
	
	@GetMapping("/getBank/{bankId}")
	public Bank retrieveBankById(@PathVariable("bankId") Long id) {
		return bankSev.findBankById(id);
	}

}
