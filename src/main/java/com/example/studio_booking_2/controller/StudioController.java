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
import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.service.StudioService;

@RestController
@RequestMapping("/api/studios")
public class StudioController {
	
	@Autowired
	private StudioService studioService;
	
	@CrossOrigin(origins = "http://localhost:5173")
	@GetMapping
	public List<StudioDto> getAllStudios(){
		return studioService.getAllStudiosIncludingInactive();
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Studio> getStudio(@PathVariable Long id) {
	    return studioService.getStudioById(id)
	            .map(ResponseEntity::ok)
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/my-studios")
	@PreAuthorize("hasAuthority('OWNER')")
	public List<Studio> getMyStudios(Authentication authentication) {
		return studioService.getStudiosByOwner(authentication);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('OWNER')")
	public ResponseEntity<?> createStudio(
	    @RequestBody StudioRequest request,
	    Authentication authentication
	) {
	    studioService.createStudio(request, authentication);
	    return ResponseEntity.ok("錄音室建立成功！");
	}
	
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('OWNER')")
	public ResponseEntity<Studio> updateStudio(@PathVariable Long id, @RequestBody StudioRequest request, Authentication authentication) {
		
		Studio updated = studioService.updateStudio(id, request, authentication);
		return ResponseEntity.ok(updated);
	}
	
	
	// 啟用錄音室
	@PutMapping("/{id}/activate")
	@PreAuthorize("hasAuthority('OWNER')")
	public ResponseEntity<String> activateStudio(@PathVariable Long id, Authentication authentication) {
		studioService.activateStudio(id, authentication);
		return ResponseEntity.ok("錄音室已啟用");
	}
	
	// 停用錄音室
	@PutMapping("/{id}/deactivate")
	@PreAuthorize("hasAuthority('OWNER')")
	public ResponseEntity<String> deactivateStudio(@PathVariable Long id, Authentication authentication) {
		studioService.deactivateStudio(id, authentication);
		return ResponseEntity.ok("錄音室已停用");
	}
	
	// 刪除錄音室（真正從資料庫刪除）
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('OWNER')")
	public ResponseEntity<String> deleteStudio(@PathVariable Long id, Authentication authentication) {
		studioService.deleteStudio(id, authentication);
		return ResponseEntity.ok("錄音室已刪除");
	}
	
	

}
