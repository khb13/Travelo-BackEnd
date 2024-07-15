package com.mysite.travelo.yeon.mail;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mysite.travelo.yeon.user.UserService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender javaMailSender;
	private String authNum;
	
	public MimeMessage createMessage(String to) throws MessagingException,
	UnsupportedEncodingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		
		message.addRecipients(Message.RecipientType.TO, to);  // 보내는 대상
		message.setSubject("회원가입 이메일 인증");
		
		String msg = "";
		msg += "<div style='margin:100px';>";
		msg += "<h1>안녕하세요</h1>";
		msg += "<br>";
		msg += "<p>아래 코드를 회원가입 창에서 입력해주세요</p>";
		msg += "<br>";
		msg += "<div align='center' style='border: 1px solid black;>";
		msg += "<h3 style='color:blue;>회원가입 인증 코드입니다.</h3>";
		msg += "<div style='font-size:130#'>";
		msg += "CODE : <strong>";
		msg += authNum + "</strong>";
		msg += "</div>";
		message.setText(msg, "utf-8", "html");
		message.setFrom(new InternetAddress("springb88t@gmail.com", "BulletBox"));
		
		return message;
	}
	
	public String createCode() {
		Random random = new Random();
		StringBuffer key = new StringBuffer();
		
		for (int i = 0; i < 8; i++) {
			int index = random.nextInt(3);
			
			switch (index) {
				case 0 -> key.append((char) ((int)random.nextInt(26) + 97));
				case 1 -> key.append((char) ((int)random.nextInt(26) + 65));
				case 2 -> key.append(random.nextInt(9));
			}
		}
		
		return authNum = key.toString();
	}
	
	public String sendSimpleMessage(String sendEmail) throws Exception {
		authNum = createCode();
		
		MimeMessage message = createMessage(sendEmail);
		
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		
		return authNum;
	}
	
}
