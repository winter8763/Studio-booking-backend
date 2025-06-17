package com.example.studio_booking_2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.studio_booking_2.model.Payment;
import com.example.studio_booking_2.model.PaymentMethod;
import com.example.studio_booking_2.repository.PaymentRepository;

@Configuration
public class DataInitializer {
	
	@Bean
	public CommandLineRunner initPayments(PaymentRepository paymentRepository) {
		return args -> {
	        if (paymentRepository.count() == 0) {
	            paymentRepository.save(new Payment(null, "現金", PaymentMethod.CASH));
	            paymentRepository.save(new Payment(null, "信用卡", PaymentMethod.CREDIT_CARD));
	            paymentRepository.save(new Payment(null, "LINE Pay", PaymentMethod.LINE_PAY));
	            paymentRepository.save(new Payment(null, "Apple Pay", PaymentMethod.APPLE_PAY));
	            paymentRepository.save(new Payment(null, "Google Pay", PaymentMethod.GOOGLE_PAY));
	            paymentRepository.save(new Payment(null, "銀行轉帳", PaymentMethod.BANK_TRANSFER));
	            System.out.println("✅ 預設付款方式已寫入資料庫！");
	        }
		};
	}

}
