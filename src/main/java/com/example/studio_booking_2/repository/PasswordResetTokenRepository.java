package com.example.studio_booking_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studio_booking_2.model.PasswordResetToken;
import com.example.studio_booking_2.model.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	
	Optional<PasswordResetToken> findByToken(String token);
	
	void deleteByUser(User user);

}
