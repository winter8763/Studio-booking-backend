package com.example.studio_booking_2.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.studio_booking_2.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private final String secretKey = "12345678901234567890123456789012"; // 32位元長度以上
	
	private final long expiration = 1000*60*60; // 1小時
	
	private Key getSignKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
		
	}
	
	public String generateToken(User user) {
		return Jwts.builder()
				.setSubject(user.getEmail()) // 仍以 email 作為主要身分
				.claim("role", user.getRole().name()) // 加上角色資訊，像 "MEMBER" 或 "OWNER"
				.setIssuedAt(new Date()) // 發出時間
				.setExpiration(new Date(System.currentTimeMillis() + expiration)) // 過期時間
				.signWith(getSignKey(), SignatureAlgorithm.HS256) // 使用金鑰簽名
				.compact(); // 產生字串
	}
	
	public String extractEmail(String token) {
		System.out.println("收到的 token 是: " + token);
		if (token == null || !token.contains(".")) {
		    throw new RuntimeException("JWT 格式不正確");
		}
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()) // 用相同密鑰驗證
				.build()
				.parseClaimsJws(token) // 解析 token
				.getBody() // 取得 claim payload
				.getSubject(); // 取出 subject（也就是 email）
	} 
	
	public String extractRole(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.get("role", String.class);
	}

}
