package com.example.studio_booking_2.controller;

import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.model.User.Role;
import com.example.studio_booking_2.repository.UserRepository;
import com.example.studio_booking_2.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            // 驗證帳號密碼
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            // 找出使用者
            User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("找不到帳號"));

            // 驗證角色
            if (user.getRole() != Role.OWNER) {
                return ResponseEntity.status(403).body("不是管理員，無法登入");
            }

            // 發送 JWT
            String jwt = jwtService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("帳號或密碼錯誤");
        }
    }

    // 內部類別（或可放在 dto 資料夾）
    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token) {}
}
