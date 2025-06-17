package com.example.studio_booking_2.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

	private final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    // 保存驗證碼
    public void saveCaptcha(String token, String code) {
        captchaStore.put(token, code.toLowerCase());
        // 可加入過期處理
    }

    // 驗證並刪除
    public boolean validate(String token, String input) {
        String real = captchaStore.get(token);
        if (real != null && real.equals(input.toLowerCase())) {
            captchaStore.remove(token);
            return true;
        }
        return false;
    }
	
}
