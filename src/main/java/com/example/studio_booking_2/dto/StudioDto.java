package com.example.studio_booking_2.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import com.example.studio_booking_2.model.Studio;

import lombok.Data;

@Data
public class StudioDto {
	
	private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private Boolean isActive;
    private List<Integer> openDays;
    private LocalTime openStart;
    private LocalTime openEnd;
    private BigDecimal price;
    private String info;
    private String notice;
    private String equipment;

    public StudioDto(Studio studio) {
        this.id = studio.getId();
        this.name = studio.getName();
        this.description = studio.getDescription();
        this.imgUrl = studio.getImgUrl();
        this.isActive = studio.getIsActive();
        this.openDays = studio.getOpenDays();
        this.openStart = studio.getOpenStart();
        this.openEnd = studio.getOpenEnd();
        this.price = studio.getPrice();
        this.info = studio.getInfo();
        this.notice = studio.getNotice();
        this.equipment = studio.getEquipment();
    }

}
