package com.example.studio_booking_2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;

@Repository
public interface StudioRepository extends JpaRepository<Studio, Long>{
	
	List<Studio> findByIsActiveTrue();
	List<Studio> findByOwner(User owner);

}
