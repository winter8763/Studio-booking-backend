package com.example.studio_booking_2.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
	
	private final String SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";
    private final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyToken(String token) {
        RestTemplate rest = new RestTemplate();
        Map<String, String> params = Map.of(
            "secret", SECRET_KEY,
            "response", token
        );

        ResponseEntity<Map> response = rest.postForEntity(VERIFY_URL + "?secret={secret}&response={response}", null, Map.class, params);
        return (Boolean) response.getBody().get("success");
    }

}
