package com.mysite.travelo.yeon.mail;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;

@Configuration
public class MailConfig {

	@Value("${MAIL_USERNAME}")
	private String username;
	
	@Value("${MAIL_PASSWORD}")
	private String password;
	
	@Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");	
        javaMailSender.setUsername(username);	
        javaMailSender.setPassword(password);		

        javaMailSender.setPort(587);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.starttls.trust","smtp.gmail.com");	//네이버의 경우 stmp.naver.com 변경
        properties.setProperty("mail.smtp.starttls.enable","true");	//starttls <-> ssl로 변경도 확인(*starttls 와 ssl은 추후 다루도록 하겠습니다.)
        return properties;
    }

}
