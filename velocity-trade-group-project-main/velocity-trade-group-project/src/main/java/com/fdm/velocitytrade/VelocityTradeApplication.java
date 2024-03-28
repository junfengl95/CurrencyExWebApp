package com.fdm.velocitytrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fdm.velocitytrade.security.RsaKeyProperties;

/**
 * The main class for the Video On Demand (VOD) application. This class serves
 * as the entry point for the Spring Boot application.
 * 
 * @author junfeng.lee
 * @version 0.01
 * @since 09/01/2024
 */
@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableScheduling
public class VelocityTradeApplication {

	public static void main(String[] args) {

		SpringApplication.run(VelocityTradeApplication.class, args);

	}

}
