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

    @GetMapping("/image")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String captchaText = producer.createText();
        request.getSession().setAttribute("captcha", captchaText.toLowerCase());

        BufferedImage image = producer.createImage(captchaText);

        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        System.out.println("🎯 Captcha 設定成功：" + captchaText);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
        out.close();
        

        
    }
    
    @GetMapping("/token")
    public ResponseEntity<?> generateCaptcha() throws IOException {
        String text = producer.createText();
        String token = UUID.randomUUID().toString();
        captchaService.saveCaptcha(token, text);

        BufferedImage image = producer.createImage(text);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "image", "data:image/jpeg;base64," + base64
        ));
    }

}
