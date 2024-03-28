package com.fdm.velocitytrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.service.TradeService;
import com.fdm.velocitytrade.service.UserService;
import com.fdm.velocitytrade.transaction.Transactions;

//@RunWith(SpringRunner.class)
//@SpringBootTest(
//  webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@SpringBootTest(
//		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//		classes = Application.class)
//@AutoConfigureMockMvc
//@TestPropertySource(
//  locations = "classpath:application-integrationtest.properties")

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class GlobalsTests {

	@Autowired
	Global global;

	@MockBean
	TradeService tradeService;

	@BeforeEach
	void initialise() {

	}

	@Test
	@DisplayName("VM connect to datasource")
	void contextLoads() {

		// if fails, most likely your VM args do not contain username and password.
		// JUnit has separate VM args compared to Java app.
		assertEquals(1, 1);

	}

	@Test
	@DisplayName("Test round with default significnat figures round up")
	void testRound1() {

		// Arrange
		// act
		double value = 200_123_456.58;
		double expected = 200_123_456.60;
		double ret = global.round(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test round with default significnat figures round down")
	void testRound2() {

		// Arrange
		// act
		double value = 200_123.456_75;
		double expected = 200_123.456_8;
		double ret = global.round(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test round with default significnat figures round up with decimals")
	void testRound3() {

		// Arrange
		// act
		double value = 0.000_123_456_789_1888;
		double expected = 0.000_123_456_789_2;
		double ret = global.round(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test round with specified significnat figures round up ")
	void testRound4() {

		// Arrange
		// act
		int sigFig = 2;
		double value = 0.000_123_456_789_1888;
		double expected = 0.000_12;
		double ret = global.round(value, sigFig);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test round with specified significnat figures round up ")
	void testRound5() {

		// Arrange
		// act
		int sigFig = 2;
		double value = 0.000_123_456_789_1888;
		double expected = 0.000_12;
		double ret = global.round(value, sigFig);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test is zero for definition in global which returns zero")
	void testIsZero1() {

		// Arrange
		// act
		double value = 0.000_000_123_456_789_1888;
		double expected = 0;
		double ret = global.isZero(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test is zero for definition in global which does not return zero")
	void testIsZero2() {

		// Arrange
		// act
		double value = 0.000_001_123_456_789_1888;
		double expected = 0.000_001_123_456_789_1888;
		double ret = global.isZero(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test is zero for definition in global which returns zero on a negative number")
	void testIsZero3() {

		// Arrange
		// act
		double value = -0.000_000_123_456_789_1888;
		double expected = 0;
		double ret = global.isZero(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test is zero for definition in global which returns zero on a negative number")
	void testIsZero4() {

		// Arrange
		// act
		double value = -555_555_555;
		double expected = -555_555_555;
		double ret = global.isZero(value);

		// Assert
		assertEquals(expected, ret);

	}

	@Test
	@DisplayName("Test is zero for definition in global which returns zero on a negative number")
	void testIsZero5() {

		// Arrange
		// act
		double value = -0.000_000_100;
		double expected = 0;
		double ret = global.isZero(value);

		// Assert
		assertEquals(expected, ret);

	}

}
