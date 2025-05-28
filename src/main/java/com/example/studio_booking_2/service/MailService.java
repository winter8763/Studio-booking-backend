package com.example.studio_booking_2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendVerificationEmail(String toEmail, String verificationUrl) {
		
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(toEmail);
			helper.setSubject("錄音室會員註冊驗證信");
			helper.setText(
					 "<h3>請點選以下連結完成帳號驗證：</h3>" +
				     "<a href=\"" + verificationUrl + "\">驗證我的帳號</a>", true
				     );
			
			mailSender.send(message);
			
		} catch(MessagingException e) {
			throw new RuntimeException("寄送email失敗:" + e.getMessage());
		}
		
	}
	
	public void sendEmail(String toEmail, String subject, String htmlContent) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(toEmail);
			helper.setFrom("yirou419lin@gmail.com", "錄音室預約系統");
			helper.setSubject(subject);
			helper.setText(htmlContent, true); // 第二個參數 true 表示為 HTML
			
			mailSender.send(message);
		} catch(Exception e) {
			throw new RuntimeException("寄送email失敗:" + e.getMessage());
		}
	}

}

