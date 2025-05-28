//package com.example.studio_booking_2.model;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "reservations")
//public class Reservation {
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	
//	@ManyToOne
//	@JoinColumn(name = "user_id")
//	private User user;
//	
//	@ManyToOne
//	@JoinColumn(name = "studio_id")
//	private Studio studio;
//	
//	@OneToOne
//	@JoinColumn(name = "paid_id", nullable = true)
//	private Payment payment;
//	
//	private LocalDate date;
//	private LocalTime startTime;
//	private LocalTime endTime;
//	
//	@Enumerated(EnumType.STRING)
//	private ReservationStatus status;
//	
//	private BigDecimal price;
//	
//	@Column(name = "create_at")
//	private LocalDateTime createAt;
//	
//	@PrePersist
//	
//	
//
//}
