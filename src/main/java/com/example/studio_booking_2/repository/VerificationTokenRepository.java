package com.example.studio_booking_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.model.VerificationToken;

import jakarta.transaction.Transactional;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
	
	Optional<VerificationToken> findByToken(String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM VerificationToken v WHERE v.user = :user")
	void deleteByUser(User user);
}

