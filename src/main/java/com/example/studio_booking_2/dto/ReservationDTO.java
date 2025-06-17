package com.example.studio_booking_2.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDTO {

	private Long id;
    private String studioName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Integer price;
    private String paymentMethod;
}
