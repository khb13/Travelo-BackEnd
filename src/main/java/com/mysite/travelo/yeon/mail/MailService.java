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

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);  // 보내는 대상
        message.setSubject("회원가입 이메일 인증");

        // 생성된 인증 코드를 가져온다
        this.authNum = createCode();

        String msg = "<html><body style='font-family: Arial, sans-serif;'>";
        msg += "<div style='margin: 50px auto; width: 80%; text-align: center; border: 1px solid #ccc; padding: 20px;'>";
        msg += "<h1 style='color: #007BFF;'>안녕하세요!</h1>";
        msg += "<p style='font-size: 18px;'>아래 인증 코드를 회원가입 창에서 입력해주세요</p>";
        msg += "<div style='background-color: #f0f0f0; padding: 10px;'>";
        msg += "<h3 style='color: #007BFF;'>회원가입 인증 코드</h3>";
        msg += "<p style='font-size: 24px; font-weight: bold;'>" + authNum + "</p>";
        msg += "</div>";
        msg += "</div>";
        msg += "</body></html>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("springb88t@gmail.com", "Travelo"));

        return message;
    }

    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }

        return key.toString();
    }

    public String sendSimpleMessage(String sendEmail) throws Exception {
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
