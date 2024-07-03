package com.mysite.travelo.yeon.member;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	
	public Member getMember(String username) {
		
		Optional<Member> member = this.memberRepository.findByusername(username);
		
		if (member.isPresent()) {
			return member.get();
		} else {
			return null;
		}
		
	}
	
	public Member getMember2(String nickname) {
		
		Optional<Member> member = this.memberRepository.findBynickname(nickname);
		
		if (member.isPresent()) {
			return member.get();
		} else {
			return null;
		}
		
	}
	
	public void create(Map<String, String> map) {
		
		Member member = new Member();
		member.setUsername(map.get("email"));
		member.setNickname(map.get("nickname"));
		member.setRegisterDate(LocalDateTime.now());
		member.setDelYn("N");
		member.setAdminYn("N");
		this.memberRepository.save(member);
		
	}
	
}
