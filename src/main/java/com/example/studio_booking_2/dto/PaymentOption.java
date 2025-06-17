package com.example.studio_booking_2.dto;

import com.example.studio_booking_2.model.PaymentMethod;

public class PaymentOption {
	
	private Long id;
	
	private String name;
	
	private PaymentMethod method;
	
	public PaymentOption(Long id, String name, PaymentMethod method) {
		this.id = id;
		this.name = name;
		this.method = method;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public PaymentMethod getMethod() { 
        return method;
    }
    
    public void setMethod(PaymentMethod method) { 
        this.method = method;
    }

	
}
