package com.example.studio_booking_2.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
	private String name;
	private String email;
	private String password;

}
