package com.example.studio_booking_2.dto;

import lombok.Data;

@Data
public class LoginRequest {
	
	private String username;
	private String password;
	private String recaptcha;
	private String captcha;
	private String captchaToken;

}
