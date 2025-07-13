package com.karrardelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KarrarDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(KarrarDeliveryApplication.class, args);
	}

}
