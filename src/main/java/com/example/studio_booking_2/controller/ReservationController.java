package com.example.studio_booking_2.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.example.studio_booking_2.dto.ReservationDTO;
import com.example.studio_booking_2.dto.ReservationRequest;
import com.example.studio_booking_2.dto.ReservationResponse;
import com.example.studio_booking_2.model.Reservation;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.ReservationRepository;
import com.example.studio_booking_2.security.CustomUserDetails;
import com.example.studio_booking_2.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@PostMapping
	public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request,
												@AuthenticationPrincipal UserDetails user) {
		
		
		
		try {
			Reservation res = reservationService.createReservation(request, user.getUsername());
			return ResponseEntity.ok(res);
		} catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal UserDetails user) {
		List<ReservationResponse> myReservations = reservationService.getReservationByUser(user.getUsername());
		return ResponseEntity.ok(myReservations);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> cancelReservation(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
		try {
			reservationService.cancelReservation(id, user.getUsername());
			return ResponseEntity.ok("預約已取消");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasRole('OWNER')")
	@GetMapping("/incoming")
	public ResponseEntity<List<ReservationResponse>> getAllReservationsForOwner(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String ownerEmail = userDetails.getUsername();
		List<ReservationResponse> incoming = reservationService.getReservationsForOwner(ownerEmail);
		return ResponseEntity.ok(incoming);
	}
	
	@PreAuthorize("hasRole('OWNER')")
	@PutMapping("/{id}/confirm")
	public ResponseEntity<?> confirmReservation (@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			reservationService.confirmReservation(id, userDetails.getUsername());
			return ResponseEntity.ok("預約已確定");
		} catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id, Authentication auth) {
        Reservation r = reservationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "預約不存在"));

        // 🔐 確認只有會員本人或 OWNER 可以查看（選用）
        String email = auth.getName();
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        ReservationResponse dto = new ReservationResponse(r);


        return ResponseEntity.ok(dto);
    }
	
	
	

}
