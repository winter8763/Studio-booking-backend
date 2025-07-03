package com.example.studio_booking_2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studio_booking_2.dto.PaymentOption;
import com.example.studio_booking_2.model.Payment;
import com.example.studio_booking_2.repository.PaymentRepository;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@GetMapping
	public List<PaymentOption> getAllPayments() {
		List<Payment> payments = paymentRepository.findAll();
	    System.out.println("目前付款方式資料筆數: " + payments.size());

		return paymentRepository.findAll()
				.stream()
				.filter(p -> p.getMethod() != null) //過濾掉沒有付款方式（method 為 null）的資料
				.map(p -> new PaymentOption(p.getId(), p.getName(), p.getMethod())) //把每筆資料轉成 PaymentOption 這個 DTO
				.toList();
	}

}
