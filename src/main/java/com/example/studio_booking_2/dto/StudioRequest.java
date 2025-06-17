package com.example.studio_booking_2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class StudioRequest {

	private String name;

	private String description;

	private String imgUrl;

	private Boolean isActive;

	private List<Integer> openDays; // 例如: [1, 3, 5] 代表星期一三五開放（0=星期日，6=星期六）

	private LocalTime openStart;

	private LocalTime openEnd;

	private BigDecimal price;

	private LocalDateTime createAt;
	
	private String info;
	
	private String notice;
	
	private String equipment;

}
