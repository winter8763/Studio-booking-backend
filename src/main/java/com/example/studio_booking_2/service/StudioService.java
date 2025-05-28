package com.example.studio_booking_2.service;

import java.text.Collator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.studio_booking_2.dto.StudioDto;
import com.example.studio_booking_2.dto.StudioRequest;
import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.repository.UserRepository;

@Service
public class StudioService {
	
	@Autowired
	private StudioRepository studioRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public List<StudioDto> getAllStudiosIncludingInactive() {
		return studioRepository.findAll().stream()
				.map(StudioDto::new)
				.collect(Collectors.toList());
	}
	
	public void createStudio(StudioRequest request, Authentication authentication) {
	    String email = authentication.getName();
	    User owner = userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("使用者不存在"));
		
		Studio studio = new Studio();
		studio.setName(request.getName());
		studio.setDescription(request.getDescription());
		studio.setImgUrl(request.getImgUrl());
		studio.setPrice(request.getPrice());
		studio.setIsActive(true);
		studio.setOpenDays(request.getOpenDays());
		studio.setOpenStart(request.getOpenStart());
		studio.setOpenEnd(request.getOpenEnd());
		studio.setOwner(owner);
		
		studioRepository.save(studio);
	}
	
	public Optional<Studio> getStudioById(Long id){
		return studioRepository.findById(id);
	}
	
	public List<Studio> getStudiosByOwner(Authentication authentication) {
		String email = authentication.getName();
		User owner = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("使用者不存在"));
		
		return studioRepository.findByOwner(owner);
	}
	
	public Studio updateStudio(Long id, StudioRequest request, Authentication authentication) {
		Studio studio = studioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Studio not found"));
		
		if(!studio.getOwner().getEmail().equals(authentication.getName())) {
			throw new RuntimeException("無權操作此錄音室");
		}
				
		studio.setName(request.getName());
		studio.setDescription(request.getDescription());
		studio.setImgUrl(request.getImgUrl());
		studio.setIsActive(request.getIsActive());
		studio.setOpenDays(request.getOpenDays());
		studio.setOpenStart(request.getOpenStart());
		studio.setOpenEnd(request.getOpenEnd());
		studio.setPrice(request.getPrice());
		
		return studioRepository.save(studio);
		
	}
	
	private Studio findOwnedStudio(Long id, Authentication authentication) {
		Studio studio = studioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Studio not found"));
	    if (!studio.getOwner().getEmail().equals(authentication.getName())) {
	        throw new RuntimeException("無權操作此錄音室");
	    }

	    return studio;
	}
	
	// 關閉錄音室
	public void deactivateStudio(Long id, Authentication authentication) {
	    Studio studio = findOwnedStudio(id, authentication);
	    studio.setIsActive(false);
	    studioRepository.save(studio);
	}
	
	// 重新啟用錄音室
	public void activateStudio(Long id, Authentication authentication) {
	    Studio studio = findOwnedStudio(id, authentication);
	    studio.setIsActive(true);
	    studioRepository.save(studio);
	}
	
	// 刪除錄音室
	public void deleteStudio(Long id, Authentication authentication) {
	    Studio studio = findOwnedStudio(id, authentication);
	    studioRepository.delete(studio);
	}

}
