package com.example.studio_booking_2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.studio_booking_2.dto.ReservationRequest;
import com.example.studio_booking_2.dto.ReservationResponse;
import com.example.studio_booking_2.model.Payment;
import com.example.studio_booking_2.model.Reservation;
import com.example.studio_booking_2.model.ReservationStatus;
import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.PaymentRepository;
import com.example.studio_booking_2.repository.ReservationRepository;
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MailService mailService;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private StudioRepository studioRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
	
    public Reservation createReservation(ReservationRequest req, String username) {
    	
    	System.out.println("🔍 傳入的 username = " + username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        Studio studio = studioRepository.findById(req.getStudioId())
                .orElseThrow(() -> new RuntimeException("找不到錄音室"));
        
        // 檢查是否重疊
        List<Reservation> conflict = reservationRepository
            .findByStudioIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                req.getStudioId(), req.getDate(), req.getEndTime(), req.getStartTime());

        if (!conflict.isEmpty()) {
            throw new RuntimeException("該時段已被預約");
        }

        // 建立預約
        Reservation res = new Reservation();
        res.setUser(user);
        res.setStudio(studio);
        res.setName(req.getName());
        res.setPhone(req.getPhone());
        res.setDate(req.getDate());
        res.setStartTime(req.getStartTime());
        res.setEndTime(req.getEndTime());
        res.setStatus(ReservationStatus.CONFIRMED);
        res.setPrice(studio.getPrice());

        // 儲存資料
        Reservation saved = reservationRepository.save(res);

        // 發送 Email 通知
        String content = """
            <h3>您好，您的錄音室預約已成立：</h3>
            <p>🎤 錄音室：%s</p>
            <p>📅 日期：%s</p>
            <p>🕒 時間：%s ~ %s</p>
            <p>💰 金額：NT$%s</p>
            <br/>
            <p>感謝使用錄音室預約系統！</p>
            """.formatted(
                studio.getName(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getPrice()
            );

        mailService.sendReservationConfirmation(
            user.getEmail(), 
            "【錄音室預約成功通知】", 
            content
        );
        

        return saved;
    }

	
	public List<ReservationResponse> getReservationByUser(String email) {
	    User user = userRepository.findByEmail(email).orElseThrow();

	    return reservationRepository.findByUserIdWithPaymentOrderByDateDesc(user.getId())
	        .stream()
	        .map(r -> ReservationResponse.builder()
	            .id(r.getId())
	            .date(r.getDate())
	            .startTime(r.getStartTime())
	            .endTime(r.getEndTime())
	            .status(r.getStatus())
	            .price(r.getPrice())
	            .studioName(r.getStudio().getName())
	            .paymentMethod(r.getPayment() != null ? r.getPayment().getMethod().name() : null)
	            .paymentName(r.getPayment() != null ? r.getPayment().getName() : null)
	            .build()
	        )
	        .collect(Collectors.toList()); // ✅ 重點在這
	}
	
	public void cancelReservation(Long reservationId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow();
		
		Reservation res = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new RuntimeException("預約不存在"));
		
	    boolean isMember = res.getUser().getEmail().equals(email);
	    boolean isOwner = res.getStudio().getOwner().getEmail().equals(email);

		
		// 確保是本人才能取消預約
		if(!isMember && !isOwner) {
			throw new RuntimeException("你沒有權限取消此預約");
		}
		
		// 將狀態改為 CALCELLED，不做硬刪除
		res.setStatus(ReservationStatus.CANCELLED);
		reservationRepository.save(res);
	}
	
	public List<ReservationResponse> getReservationsForOwner(String ownerEmail){
		// 找出該 OWNER 擁有的錄音室
		User owner = userRepository.findByEmail(ownerEmail)
				.orElseThrow(() -> new RuntimeException("找不到該使用者"));
		List<Studio> studios = studioRepository.findByOwner(owner);
		
		List<Reservation> reservations = reservationRepository.findIncomingReservationsWithDetails(studios);
		
		return reservations.stream()
				.map(ReservationResponse::new)
				.collect(Collectors.toList());
	}
	
	public void confirmReservation(Long id, String ownerEmail) {
		Reservation res = reservationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到預約"));
		
		// 驗證這筆預約的錄音室是不是屬於這位OWNER的
		if (!res.getStudio().getOwner().getEmail().equals(ownerEmail)) {
			throw new RuntimeException("無權確認這筆預約");
		} 
		if (res.getStatus() == ReservationStatus.CANCELLED) {
			throw new RuntimeException("此預約已取消");
		}
		
		res.setStatus(ReservationStatus.CONFIRMED);
		reservationRepository.save(res);
		
	}


}
