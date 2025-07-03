package com.example.studio_booking_2.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studio_booking_2.service.CaptchaService;
import com.google.code.kaptcha.impl.DefaultKaptcha;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private DefaultKaptcha producer;
    
    @Autowired
    private CaptchaService captchaService;
    
    @GetMapping("/token")
    public ResponseEntity<?> generateCaptcha() throws IOException {
        String text = producer.createText();
        String token = UUID.randomUUID().toString();
        captchaService.saveCaptcha(token, text);
        
        //把剛剛產生的驗證碼文字轉成圖片（BufferedImage），然後寫入 byte array，最後編碼成 Base64 字串，這樣前端可以直接顯示這張圖片
        BufferedImage image = producer.createImage(text);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        
        System.out.println("🎯 Captcha 設定成功：" + text);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "image", "data:image/jpeg;base64," + base64
        ));
    }

}
