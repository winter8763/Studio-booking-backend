package com.example.studio_booking_2.service;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.studio_booking_2.dto.StudioDto;
import com.example.studio_booking_2.dto.StudioRequest;
import com.example.studio_booking_2.model.Studio;
import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.ReservationRepository;
import com.example.studio_booking_2.repository.StudioRepository;
import com.example.studio_booking_2.repository.UserRepository;

@Service
public class StudioService {
	
	@Autowired
	private StudioRepository studioRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	// 列出資料庫中所有錄音室（包含停用的）
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
		studio.setInfo(request.getInfo());
		studio.setNotice(request.getNotice());
		studio.setEquipment(request.getEquipment());
		
		studioRepository.save(studio);
	}
	
	public Optional<Studio> getStudioById(Long id){
		return studioRepository.findById(id);
	}
	
	public List<Studio> getStudiosByOwner(Authentication authentication) { //取得目前登入者擁有的所有錄音室
		String email = authentication.getName(); //取出登入者資訊
		User owner = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("使用者不存在"));
		
		return studioRepository.findByOwner(owner);
	}
	
	public Optional<StudioDto> getStudioByIdForOwner(Long id, String email) { //根據 ID 取得某間錄音室（DTO）
	    return studioRepository.findById(id)
	            .map(StudioDto::new);
	}
	
	public Studio updateStudio(Long id, StudioRequest request, Authentication authentication) {
		Studio studio = studioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Studio not found"));
		
		if(!studio.getOwner().getEmail().equals(authentication.getName())) {
			throw new RuntimeException("無權操作此錄音室");
		}
		if (request.getIsActive() != null) {
		    studio.setIsActive(request.getIsActive());
		}
				
		studio.setName(request.getName());
		studio.setDescription(request.getDescription());
		studio.setImgUrl(request.getImgUrl());
		studio.setIsActive(request.getIsActive());
		studio.setOpenDays(request.getOpenDays());
		studio.setOpenStart(request.getOpenStart());
		studio.setOpenEnd(request.getOpenEnd());
		studio.setPrice(request.getPrice());
	    studio.setInfo(request.getInfo());
	    studio.setNotice(request.getNotice());
	    studio.setEquipment(request.getEquipment());
		
		return studioRepository.save(studio);
		
	}
	
	private Studio findOwnedStudio(Long id, Authentication authentication) { //確保只有錄音室的擁有者（OWNER）才能操作該錄音室資料
		Studio studio = studioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Studio not found"));
	    if (!studio.getOwner().getEmail().equals(authentication.getName())) {
	        throw new RuntimeException("無權操作此錄音室");
	    }

	    return studio;
	}
	
	public List<String> getAvailableDates(Long studioId) {
        Studio studio = studioRepository.findById(studioId).orElseThrow();

        List<Integer> openDays = studio.getOpenDays(); // 0=週日, ..., 6=週六

        // 設定查詢範圍：從今天起算 14 天
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(14);

        // 把已經有預約的日期查出來
        List<LocalDate> reservedDates = reservationRepository
                .findDistinctDateByStudioId(studioId);
        Set<String> reservedSet = reservedDates.stream()
                .map(LocalDate::toString) // yyyy-MM-dd
                .collect(Collectors.toSet());

        // 過濾出開放日、且沒有被預約的日期
        List<String> availableDates = new ArrayList<>();

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            int dayOfWeek = date.getDayOfWeek().getValue() % 7; // 週一=1 → 1, 週日=7 → 0
            if (openDays.contains(dayOfWeek)) {
                String formatted = date.toString(); // yyyy-MM-dd
                if (!reservedDates.contains(formatted)) {
                    availableDates.add(formatted);
                }
            }
        }

        return availableDates;
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
