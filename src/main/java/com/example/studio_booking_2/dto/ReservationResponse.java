package com.example.studio_booking_2.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.example.studio_booking_2.model.Reservation;
import com.example.studio_booking_2.model.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private ReservationStatus status;

    private BigDecimal price;

    private String studioName;

    private String paymentMethod;

    private String paymentName;

    private String name; // 預約人填的名字

    private String phone; // 預約人填的電話

    private String userName;  // 使用者帳號名稱（後台看用）

    private String userEmail; // 使用者 email（後台看用）

    // ✅ 用 Reservation 自動塞入所有欄位
    public ReservationResponse(Reservation r) {
        this.id = r.getId();
        this.date = r.getDate();
        this.startTime = r.getStartTime();
        this.endTime = r.getEndTime();
        this.status = r.getStatus();
        this.price = r.getPrice();
        this.studioName = r.getStudio().getName();
        this.name = r.getName();
        this.phone = r.getPhone();
        this.userName = r.getUser().getName();
        this.userEmail = r.getUser().getEmail();
    }
}
