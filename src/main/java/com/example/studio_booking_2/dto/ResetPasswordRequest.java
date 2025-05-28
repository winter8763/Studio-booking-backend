package com.example.studio_booking_2.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private String token;
	private String newPassword;
	
	public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
