package com.example.studio_booking_2.dto;

import com.example.studio_booking_2.model.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	
    private Long id;
    private String name;
    private PaymentMethod method;

}
