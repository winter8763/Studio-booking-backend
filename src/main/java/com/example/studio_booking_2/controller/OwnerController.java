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
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.service.StudioService;

@RestController
@RequestMapping("/api/owner")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class OwnerController {
	
	@Autowired
	private StudioService studioService;
	
	@Autowired
	private StudioRepository studioRepository;
	
	
	
	@GetMapping("/studios")
	@PreAuthorize("hasRole('OWNER')") //限 OWNER 可用
	public List<Studio> getAllStudiosForOwner(Authentication auth) {
		System.out.println(" 已進入 OWNER Studios API：使用者=" + auth.getName());
	    return studioRepository.findAll(); // 回傳全部錄音室（包含 isActive=false）
	}
	
	@GetMapping("/studios/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<StudioDto> getStudioByIdForOwner(@PathVariable Long id, Authentication auth) {
	    String email = auth.getName();
	    return studioService.getStudioByIdForOwner(id, email)
	            .map(ResponseEntity::ok)
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<?> createStudio(
	    @RequestBody StudioRequest request,
	    Authentication authentication
	) {
	    studioService.createStudio(request, authentication);
	    return ResponseEntity.ok("錄音室建立成功！");
	}
	
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Studio> updateStudio(@PathVariable Long id, @RequestBody StudioRequest request, Authentication authentication) {
		
		Studio updated = studioService.updateStudio(id, request, authentication);
		return ResponseEntity.ok(updated);
	}
	
	
	// 啟用錄音室
	@PutMapping("/{id}/activate")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<String> activateStudio(@PathVariable Long id, Authentication authentication) {
		studioService.activateStudio(id, authentication);
		System.out.println("收到啟用請求：" + id);
		return ResponseEntity.ok("錄音室已啟用");
	}
	
	// 停用錄音室
	@PutMapping("/{id}/deactivate")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<String> deactivateStudio(@PathVariable Long id, Authentication authentication) {
		studioService.deactivateStudio(id, authentication);
		System.out.println("收到停用請求：" + id);
		return ResponseEntity.ok("錄音室已停用");
	}
	
	// 刪除錄音室（真正從資料庫刪除）
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<String> deleteStudio(@PathVariable Long id, Authentication authentication) {
		studioService.deleteStudio(id, authentication);
		
		return ResponseEntity.ok("錄音室已刪除");
	}
	

}
