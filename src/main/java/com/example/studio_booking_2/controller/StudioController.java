package com.example.studio_booking_2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studio_booking_2.dto.StudioDto;
import com.example.studio_booking_2.dto.StudioRequest;
import com.example.studio_booking_2.dto.StudioResponse;
import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.service.StudioService;

@RestController
@RequestMapping("/api/studios")
public class StudioController {
	
	@Autowired
	private StudioService studioService;
	
	@Autowired
	private StudioRepository studioRepository;
	
	@CrossOrigin(origins = "http://127.0.0.1:5500/")
	
	
	@GetMapping
	public List<Studio> getPublicStudios() {
	    return studioRepository.findByIsActiveTrue(); // ✅ 只回傳 isActive=true 的錄音室
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<StudioResponse> getStudioById(@PathVariable Long id) {
	    System.out.println("✅ 接收到 /api/studios/" + id);

	    return studioRepository.findById(id)
	        .map(studio -> {
	            StudioResponse response = new StudioResponse();
	            response.setId(studio.getId());
	            response.setName(studio.getName());
	            response.setDescription(studio.getDescription());
	            response.setImgUrl(studio.getImgUrl());
	            response.setPrice(studio.getPrice().intValue());
	            response.setOpenDays(studio.getOpenDays());
	            response.setOpenStart(studio.getOpenStart().toString());
	            response.setOpenEnd(studio.getOpenEnd().toString()); // ➜ "HH:mm:ss" 格式
	            response.setInfo(studio.getInfo());
	            response.setNotice(studio.getNotice());
	            response.setEquipment(studio.getEquipment());
	            return ResponseEntity.ok(response);
	        })
	        .orElse(ResponseEntity.notFound().build());
	}


	
	@GetMapping("/{id}/available-dates")
	public ResponseEntity<List<String>> getAvailableDates(@PathVariable Long id) {
		List<String> dates = studioService.getAvailableDates(id);
		return ResponseEntity.ok(dates);
	}

}
