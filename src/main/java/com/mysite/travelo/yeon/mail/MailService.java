package com.mysite.travelo.yeon.mail;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${MAIL_USERNAME}")
	private String username;
	
	private final JavaMailSender javaMailSender;
    private String authNum;

    public MimeMessage createConfirmMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);  
        message.setSubject("이메일 인증");

        // 생성된 인증 코드를 가져온다
        this.authNum = createCode();

        String msg = "<html><body style='font-family: Arial, sans-serif;'>";
        msg += "<div style='margin: 50px auto; width: 80%; text-align: center; border: 1px solid #ccc; padding: 20px;'>";
        msg += "<h1 style='color: #4d76b3;'>안녕하세요!</h1>";
        msg += "<p style='font-size: 18px;'>아래 인증 코드를 입력해주세요</p>";
        msg += "<div style='background-color: #f0f0f0; padding: 10px;'>";
        msg += "<h3 style='color: #4d76b3;'>회원가입 인증 코드</h3>";
        msg += "<p style='font-size: 24px; font-weight: bold;'>" + authNum + "</p>";
        msg += "</div>";
        msg += "</div>";
        msg += "</body></html>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(username, "travelo"));

        return message;
    }
    
    public MimeMessage createJoinMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);  
        message.setSubject("회원 가입을 축하드립니다.");

        // 생성된 인증 코드를 가져온다
        this.authNum = createCode();

        String msg = "<html><body style='font-family: Arial, sans-serif;'>";
        msg += "<div style='margin: 50px auto; width: 80%; max-width: 600px; text-align: center; border: 1px solid #ccc; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>";
        msg += "<h1 style='color: #4d76b3; margin-bottom: 20px;'>환영합니다!</h1>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>저희 travelo에 가입해 주셔서 감사합니다. 이제부터 최고의 여행 경험을 제공해 드리기 위해 최선을 다하겠습니다.</p>";
        msg += "<div style='background-color: #f9f9f9; padding: 20px; margin: 30px 0; border-radius: 10px;'>";
        msg += "<h2 style='font-size: 22px; color: #4d76b3; margin-bottom: 20px;'>travelo의 주요 기능</h2>";
        msg += "<ul style='list-style-type: none; padding: 0; font-size: 18px; color: #333; text-align: left;'>";
        msg += "<li style='margin-bottom: 10px;'>📚 체계적으로 분류된 여행 정보</li>";
        msg += "<li style='margin-bottom: 10px;'>🛠️ 쉽고 빠른 여행 계획 커스터마이징</li>";
        msg += "<li style='margin-bottom: 10px;'>💬 단순하고 간결한 정보 공유 및 소통</li>";
        msg += "</ul>";
        msg += "</div>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>언제든지 도움이 필요하시다면, 고객 지원팀(<a href='mailto:springb88t@gmail.com' style='color: #4d76b3; text-decoration: none;'>springb88t@gmail.com</a>)으로 연락 주시기 바랍니다.</p>";
        msg += "<p style='font-size: 18px; font-weight: bold; color: #333;'>travelo 운영팀 드림</p>";
        msg += "</div>";
        msg += "</body></html>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(username, "travelo"));

        return message;
    }
    
    public MimeMessage createResignMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);  // 보내는 대상
        message.setSubject("후기 신고 누적으로 인한 계정 강제 탈퇴 안내");

        // 생성된 인증 코드를 가져온다
        this.authNum = createCode();

        String msg = "<html><body style='font-family: Arial, sans-serif;'>";
        msg += "<div style='margin: 50px auto; width: 80%; max-width: 600px; text-align: center; border: 1px solid #ccc; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>";
        msg += "<h1 style='color: #4d76b3; margin-bottom: 20px;'>안녕하세요.</h1>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>저희 travelo를 이용해 주셔서 감사합니다. 귀하의 계정이 최근 후기 신고 누적으로 인해 강제 탈퇴 처리되었음을 안내드리고자 합니다.</p>";
        msg += "<div style='background-color: #f9f9f9; padding: 20px; margin: 30px 0; border-radius: 10px;'>";
        msg += "<p style='font-size: 24px; font-weight: bold; color: #e74c3c; margin: 20px 0;'>총 5회 이상의 블라인드 후기 누적</p>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>이로 인해 귀하의 계정은 더 이상 저희 플랫폼에서 사용할 수 없게 되었으며, 관련된 서비스 접근이 차단됩니다.</p>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>귀하의 계정이 탈퇴 처리된 점에 대해 안타깝게 생각하며, 향후 다른 계정으로 다시 저희 플랫폼을 이용하실 수 있기를 바랍니다.</p>";
        msg += "<p style='font-size: 18px; color: #333; line-height: 1.6;'>만약 이 결정에 대해 이의가 있거나 문의 사항이 있으신 경우, 고객 지원팀(<a href='mailto:springb88t@gmail.com' style='color: #4d76b3; text-decoration: none;'>springb88t@gmail.com</a>)으로 연락 주시기 바랍니다.</p>";
        msg += "</div>";
        msg += "<p style='font-size: 18px; font-weight: bold; color: #333;'>travelo 운영팀 드림</p>";
        msg += "</div>";
        msg += "</body></html>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(username, "travelo"));

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

    public String sendConfirmMessage(String sendEmail) throws Exception {
        MimeMessage message = createConfirmMessage(sendEmail);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return authNum;
    }
    
    public void sendJoinMessage(String sendEmail) throws Exception {
        MimeMessage message = createJoinMessage(sendEmail);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
    
    public void sendResignMessage(String sendEmail) throws Exception {
        MimeMessage message = createResignMessage(sendEmail);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
	
}
