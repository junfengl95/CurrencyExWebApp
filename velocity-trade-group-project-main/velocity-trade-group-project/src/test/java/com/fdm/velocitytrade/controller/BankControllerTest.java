package com.fdm.velocitytrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.service.BankService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BankControllerTest {

	@Autowired
	BankController bc;
	
	@MockBean
	BankService bs;
	
	@Mock
	Bank b1;
	
	@Test
	@DisplayName("VM connect to datasource")		// copy from the TradeController to test the connection to DB
	void contextLoads() {

		// if fails, most likely your VM args do not contain username and password.
		// JUnit has separate VM args compared to Java app.
		assertEquals(1, 1);

	}
	
	@Test
	@DisplayName("Test for changing the Bank name")
	void TestChangingBankName() {
		Long bankId = 1L;
        String newBankName = "HSBC";
        
        when(bs.updateBankname(bankId, newBankName)).thenReturn(b1);
        
        Bank actual = bc.updateBankName(bankId, newBankName);
        
        assertEquals(b1, actual);
	}
	
	@Test
	@DisplayName("Test for changing the Bank account number")
	void TestChangingBankAccountNumber() {
		Long bankId = 1L;
        String newAccNum = "987654321";
        
        when(bs.updateAccountNumber(bankId, newAccNum)).thenReturn(b1);
        
        Bank actual = bc.updateAccountNumber(bankId, newAccNum);
        
        assertEquals(b1, actual);
        
	}
	
}
