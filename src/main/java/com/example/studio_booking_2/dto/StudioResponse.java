package com.example.studio_booking_2.dto;

import java.util.List;

import lombok.Data;

@Data
public class StudioResponse {

    private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private List<Integer> openDays;
    private String openStart;
    private String openEnd;
    private Integer price;
    private String info;
    private String notice;
    private String equipment;
	
}
