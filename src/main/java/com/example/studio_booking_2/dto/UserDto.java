package com.example.studio_booking_2.dto;


import com.example.studio_booking_2.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	
	private Long id;
	private String name;
	private String email;
	private String role;
	private boolean isVerified;
	
	public UserDto(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.role = user.getRole().toString();
		this.isVerified = user.isVerified();
	}

}
