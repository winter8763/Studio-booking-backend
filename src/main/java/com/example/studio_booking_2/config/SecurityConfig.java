package com.example.studio_booking_2.config;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ✅ 若使用 HttpMethod 需加 import
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.studio_booking_2.security.JwtAuthFilter;

@EnableMethodSecurity // 開啟 @PreAuthorize 支援
@Configuration
public class SecurityConfig {
	
	@Autowired
    private JwtAuthFilter jwtAuthFilter;
	
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .cors(Customizer.withDefaults())
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(auth -> auth
	            // 允許 CORS 預檢請求
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

	            // ✅ 允許 admin 登入 API（不限制 method，最保險）
	            .requestMatchers("/api/admin/login", "/admin-login.html").permitAll()

	            // 公開 API
	            .requestMatchers("/api/auth/**", "/api/captcha/**").permitAll()
	            .requestMatchers("/api/auth/resend-verification").permitAll()
	            .requestMatchers("/api/payments").permitAll()
	            .requestMatchers(HttpMethod.GET, "/api/studios/**").permitAll()

	            // 預約與會員功能
	            .requestMatchers("/api/reservations/**").hasAnyRole("MEMBER", "OWNER")
	            .requestMatchers(HttpMethod.GET, "/api/payments/**").authenticated()

	            // 管理員功能
	            .requestMatchers("/api/owner/**").hasRole("OWNER")

	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	
	@Bean
	public CorsFilter corsFilter() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("http://127.0.0.1:5500"); // 前端網址
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}

}
