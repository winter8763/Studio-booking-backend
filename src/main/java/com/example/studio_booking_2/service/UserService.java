package com.example.studio_booking_2.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.studio_booking_2.dto.ChangePasswordRequest;
import com.example.studio_booking_2.dto.LoginRequest;
import com.example.studio_booking_2.dto.RegisterRequest;
import com.example.studio_booking_2.model.PasswordResetToken;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.model.User.Role;
import com.example.studio_booking_2.model.VerificationToken;
import com.example.studio_booking_2.repository.PasswordResetTokenRepository;
import com.example.studio_booking_2.repository.UserRepository;
import com.example.studio_booking_2.repository.VerificationTokenRepository;
import com.example.studio_booking_2.security.JwtService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;


@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private VerificationTokenRepository tokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	public ResponseEntity<?> register(RegisterRequest request) {
	    String email = request.getEmail();

	    User existingUser = userRepository.findByEmail(email).orElse(null);

	    if (existingUser != null) {
	        if (!existingUser.isVerified()) {
	            resendVerificationEmail(existingUser.getEmail());
	            return ResponseEntity
	                .status(HttpStatus.CONFLICT)
	                .body("帳號尚未驗證，我們已重新寄送驗證信");
	        }
	        return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body("此 Email 已經被註冊");
	    }

	    User user = new User();
	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setRole(Role.MEMBER);
	    user.setVerified(false);
	    userRepository.save(user);

	    String token = UUID.randomUUID().toString();
	    VerificationToken verificationToken = new VerificationToken(
	        null, token, user, LocalDateTime.now().plusMinutes(30)
	    );
	    tokenRepository.save(verificationToken);

	    String verificationUrl = "http://localhost:5173/verify?token=" + token;
	    mailService.sendVerificationEmail(user.getEmail(), verificationUrl);

	    return ResponseEntity.ok("註冊成功，請查收驗證信");
	}

	
	public String login(LoginRequest request) {
	    System.out.println("🔍 嘗試登入帳號：" + request.getEmail());
	    
	    User user = userRepository.findByEmail(request.getEmail())
	                .orElseThrow(() -> new RuntimeException("帳號不存在"));
	    
	    System.out.println("✅ 找到帳號：" + user.getEmail());

	    if (!Boolean.TRUE.equals(user.isVerified())) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "帳號尚未驗證");
	    }

	    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "密碼錯誤");
	    }

	    return jwtService.generateToken(user);
	}

	
	public void changePassword(String email, ChangePasswordRequest request) {
		User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("使用者不存在"));
		
		// 驗證舊密碼是否正確
		if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			throw new RuntimeException("舊密碼錯誤");
		}
		
		// 設定新密碼（記得加密）
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}
	
	public void forgotPassword(String email) {
		User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("找不到該信箱"));
		
		// 產生 Token
		String token = UUID.randomUUID().toString();
		
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(token);
		resetToken.setUser(user);
		resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
		passwordResetTokenRepository.save(resetToken);
		
		// 發送 email (你可自訂連結)
		String link = "http://localhost:5173/reset-password?token=" + token;
		mailService.sendEmail(user.getEmail(), "重設密碼連結",
				"<p>請點選以下連結來重設您的密碼：</p>" +
				"<a href=\"" + link + "\">重設密碼</a>");
		
	}
	
	public User resetPassword(String token, String newPassword) {
	    PasswordResetToken tokenEntity = passwordResetTokenRepository.findByToken(token)
	        .orElseThrow(() -> new RuntimeException("無效的重設連結"));

	    if (tokenEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
	        throw new RuntimeException("重設連結已過期");
	    }

	    User user = tokenEntity.getUser();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);

	    passwordResetTokenRepository.delete(tokenEntity); // 清掉 token 避免重複使用

	    return user; // ✅ 回傳給 Controller 用來產生 JWT
	}

	@Transactional
	public void resendVerificationEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("帳號不存在"));
		
		if(user.isVerified()) {
			throw new RuntimeException("帳號已完成驗證");
		}
		
		// 先刪除舊 Token
		tokenRepository.deleteByUser(user);
		
		VerificationToken newToken = new VerificationToken();
		newToken.setToken(UUID.randomUUID().toString());
		newToken.setUser(user);
		newToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
		tokenRepository.save(newToken);
		
		String url = "http://localhost:5173/verify?token=" + newToken.getToken();
		mailService.sendVerificationEmail(user.getEmail(), url);
	}
	
	@Transactional
	public void resendResetPassewordToken(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("查無此帳號，請確認 Email 是否正確"));
		
		if(!user.isVerified()) {
			throw new RuntimeException("帳號尚未驗證，無法重設密碼");
		}
		
		// 刪除舊的重設 Token
		passwordResetTokenRepository.deleteByUser(user);
		
		PasswordResetToken newToken = new PasswordResetToken();
		newToken.setToken(UUID.randomUUID().toString());
		newToken.setUser(user);
		newToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
		passwordResetTokenRepository.save(newToken);
		
		String resetUrl = "http://localhost:5173/api/auth/reset-password?token=" + newToken.getToken();
		
		mailService.sendEmail(user.getEmail(),
							  "【錄音室預約系統】新的重設密碼連結",
							  "<p>這是您新的密碼重設連結（30 分鐘內有效）：</p>" +
							  "<a href=\"" + resetUrl + "\">點擊重設密碼</a>"
							  );
	}
	
	public void promoteToOwner(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("找不到該使用者"));
		
		user.setRole(Role.OWNER);
		userRepository.save(user);
	}
	
//	@PostConstruct
//	public void initOwnerAccount() {
//		// 檢查是否已存在 Owner 帳號
//		Optional<User> existing = userRepository.findByEmail("winter8763@gmail.com");
//		if(existing.isPresent()) return;
//		
//		User owner = new User();
//		owner.setName("管理者");
//		owner.setEmail("winter8763@gmail.com");
//		owner.setPassword(passwordEncoder.encode("owner123"));
//		owner.setRole(Role.OWNER);
//		owner.setVerified(true);
//		owner.setBanned(false);
//		
//		userRepository.save(owner);
//		System.out.println("✅ 預設 OWNER 帳號建立完成：winter8763@gmail.com / owner123");
//	}
	
	

}

