package com.example.studio_booking_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studio_booking_2.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

}
