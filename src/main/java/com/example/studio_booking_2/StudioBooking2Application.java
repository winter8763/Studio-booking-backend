package com.example.studio_booking_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class StudioBooking2Application {

	public static void main(String[] args) {
		SpringApplication.run(StudioBooking2Application.class, args);
	}

}
