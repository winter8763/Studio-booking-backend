package com.example.studio_booking_2.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ReservationRequest {
	
	private Long studioId;
	
	private LocalDate date;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private String name;
	
	private String phone;

}
