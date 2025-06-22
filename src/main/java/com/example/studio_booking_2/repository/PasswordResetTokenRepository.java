package com.example.studio_booking_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studio_booking_2.model.PasswordResetToken;
import com.example.studio_booking_2.model.User;

import jakarta.transaction.Transactional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	
	@Modifying
	@Transactional
	@Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
	
	Optional<PasswordResetToken> findByToken(String token);

}
