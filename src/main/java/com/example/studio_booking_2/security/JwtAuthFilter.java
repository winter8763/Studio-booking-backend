package com.example.studio_booking_2.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.studio_booking_2.model.User;
import com.example.studio_booking_2.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    	
    	// 如果是登入路徑，不需要驗證 JWT，直接放行
    	String path = request.getServletPath();
    	System.out.println("目前請求路徑：" + path);
    	if ("/api/admin/login".equals(path) || "/api/auth/login".equals(path)) {
    	    filterChain.doFilter(request, response);
    	    return;
    	}

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        String email = null;
        try {
            email = jwtService.extractEmail(token); //解析出 email
        } catch (Exception e) {
            System.out.println("JWT 解碼失敗：" + e.getMessage());
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                CustomUserDetails userDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                System.out.println("權限清單：" + userDetails.getAuthorities());
                System.out.println("成功設置 SecurityContextHolder 為：" + email);
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("驗證成功，登入使用者：" + email);
                
            } else {
                System.out.println("找不到使用者：" + email);
            }
        } else if (email == null) {
            System.out.println("無法從 JWT 中解析 email");
        }
        
        

        filterChain.doFilter(request, response);
    }

}
