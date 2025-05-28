package com.example.studio_booking_2.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.studio_booking_2.dto.ChangePasswordRequest;
import com.example.studio_booking_2.dto.ForgotPasswordRequest;
import com.example.studio_booking_2.dto.LoginRequest;
import com.example.studio_booking_2.dto.RegisterRequest;
import com.example.studio_booking_2.dto.ResendResetPasswordRequest;
import com.example.studio_booking_2.dto.ResendVerificationRequest;
import com.example.studio_booking_2.dto.ResetPasswordRequest;
import com.example.studio_booking_2.dto.UserDto;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.model.VerificationToken;
import com.example.studio_booking_2.repository.UserRepository;
import com.example.studio_booking_2.repository.VerificationTokenRepository;
import com.example.studio_booking_2.security.CustomUserDetails;
import com.example.studio_booking_2.security.JwtService;
import com.example.studio_booking_2.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
	
	@Autowired
	private UserService userService;

    AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
	
	@PostMapping("/register")
	public String register(@RequestBody RegisterRequest request) {
		
		userService.register(request);
		
		
		return "註冊成功，請等待驗證。";
		
	}
	
	@PostMapping("/verify")
	public ResponseEntity<?> verify(@RequestBody Map<String, String> request) {
	    String token = request.get("token");

	    VerificationToken verificationToken = tokenRepository.findByToken(token)
	        .orElseThrow(() -> new RuntimeException("無效的驗證連結"));

	    if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
	        return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(Map.of("message", "驗證連結已過期，請重新申請"));
	    }

	    User user = verificationToken.getUser();
	    user.setVerified(true);
	    userRepository.save(user);
	    tokenRepository.delete(verificationToken);

	    return ResponseEntity.ok(Map.of("message", "帳號驗證成功，請登入"));
	}

	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
	    try {
	        String token = userService.login(request);
	        return ResponseEntity.ok(token);
	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode())
	                             .body(Map.of("message", e.getReason()));
	    } catch (Exception e) {
	        e.printStackTrace(); // ✅ 加上這行 log 詳細錯誤
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(Map.of("message", "登入發生錯誤"));
	    }
	}



	@GetMapping("/me")
	public ResponseEntity<?> getMe() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或 Token 無效");
	    }

	    Object principal = authentication.getPrincipal();

	    if (principal instanceof CustomUserDetails userDetails) {
	        User user = userDetails.getUser();
	        return ResponseEntity.ok(new UserDto(user)); // 🔥 這樣就乾淨又穩定
	    }

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登入資訊錯誤");
	}




	
	@PutMapping("/change-password")
	public String changePassword(
			@RequestBody ChangePasswordRequest request,
			@RequestHeader("Authorization") String authHeader) {
		// 從 JWT 中取得 email
		String token = authHeader.replace("Bearer ", "");
		String email = jwtService.extractEmail(token);
		
		userService.changePassword(email, request);
		return "密碼修改成功";
	}
	
	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
		userService.forgotPassword(request.getEmail());
		return "已寄出重設密碼信";
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(
	    @RequestParam("token") String token,
	    @RequestBody ResetPasswordRequest request
	) {
		userService.resetPassword(token, request.getNewPassword());
		return ResponseEntity.ok("密碼已成功重設，請重新登入");
	}
	
	@PostMapping("/resend-verification")
	public String resendVerification(@RequestBody ResendVerificationRequest request) {
		userService.resendVerificationEmail(request.getEmail());
		return "驗證信已重新寄出";
	}
	
	@PostMapping("/resend-reset-password")
	public String resendResetPassword(@RequestBody ResendResetPasswordRequest request) {
		userService.resendResetPassewordToken(request.getEmail());
		return "已寄出新的密碼重設連結";
	}
	
	@PutMapping("/promote-owner")
	public String promoteToOwner(@RequestParam String email) {
		userService.promoteToOwner(email);
		return "已成功將使用者設為Owner";
	}

}

