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
    }

    // 驗證並刪除
    public boolean validate(String token, String input) {
        String real = captchaStore.get(token);
        if (real != null && real.equalsIgnoreCase(input)) {
            captchaStore.remove(token); // 驗證成功後移除 token
            return true;
        }
        return false;
    }
	
}
