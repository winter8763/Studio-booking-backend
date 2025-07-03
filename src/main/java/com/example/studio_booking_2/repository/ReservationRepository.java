package com.example.studio_booking_2.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studio_booking_2.model.Reservation;
import com.example.studio_booking_2.model.Studio;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{
	
	// 查詢某天某間錄音室所有預約
	List<Reservation> findByStudioIdAndDate(Long studioId, LocalDate date);
	
	// 查詢是否有重疊預約
	List<Reservation> findByStudioIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
			Long studioId, LocalDate date, LocalTime endTime, LocalTime startTime);
	
	// 查出某個使用者的所有預約，依照日期由新到舊排序
	List<Reservation> findByUserIdOrderByDateDesc(Long userId);
	
	//查詢某些錄音室的所有預約（for OWNER 用）
	List<Reservation> findByStudioIn(List<Studio> studios);
	
	@Query("SELECT r FROM Reservation r " +
		       "JOIN FETCH r.studio " +
		       "LEFT JOIN FETCH r.payment " +
		       "WHERE r.user.id = :userId " +
		       "ORDER BY r.date DESC")
	//查詢使用者預約（包含錄音室與付款資料）
	List<Reservation> findByUserIdWithPaymentOrderByDateDesc(@Param("userId") Long userId);
	
	@Query("SELECT r FROM Reservation r " +
		       "JOIN FETCH r.user " +
		       "JOIN FETCH r.studio " +
		       "LEFT JOIN FETCH r.payment " +
		       "WHERE r.studio IN :studios")
	//OWNER 查詢某些錄音室收到的所有預約（含會員與付款）
	List<Reservation> findIncomingReservationsWithDetails(@Param("studios") List<Studio> studios);
	
	@Query("SELECT DISTINCT r.date FROM Reservation r WHERE r.studio.id = :studioId")
	//查詢某間錄音室已經有哪些日期被預約過
    List<LocalDate> findDistinctDateByStudioId(@Param("studioId") Long studioId);

}
