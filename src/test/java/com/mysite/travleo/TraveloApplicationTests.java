package com.mysite.travleo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.travleo.yeon.member.Member;
import com.mysite.travleo.yeon.member.MemberRepository;

@SpringBootTest
class TraveloApplicationTests {

	@Autowired
	private MemberRepository memberRepository;
	
	@Test
	void contextLoads() {
		
		Member m1 = new Member();
		m1.setUsername("yeon@gmail.com");
		m1.setNickname("yeon");
		m1.setRegisterDate(LocalDateTime.now());
		m1.setDelYN("N");
		m1.setAdminYN("Y");
		
		this.memberRepository.save(m1);
		
	}

}
