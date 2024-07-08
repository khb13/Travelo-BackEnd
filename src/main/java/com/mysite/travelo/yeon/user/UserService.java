package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public boolean checkUsernameDuplicate(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public void securityJoin(JoinRequest joinRequest) {
		if (userRepository.existsByUsername(joinRequest.getUsername())) {
			return;
		}
		
		joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));
		
		SiteUser user = new SiteUser();
		user.setUsername(joinRequest.getUsername());
		user.setPassword(joinRequest.getPassword());
		user.setTel(joinRequest.getTel());
		user.setRole(UserRole.USER);
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		
		userRepository.save(user);
	}
	
	public SiteUser login(LoginRequest loginRequest) {
		Optional<SiteUser> findUser = userRepository.findByUsername(loginRequest.getUsername());
		
		if (findUser.get() == null) {
			return null;
		}
		
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), findUser.get().getPassword())) {
			return null;
		}
		
		if ("Y".equals(findUser.get().getDelYn())) {
			return null;
		}
		
		return findUser.get();
	}
	
	public SiteUser getLoginUserByUsername(String username) {
		if (username == null) return null;
		
		Optional<SiteUser> findUser = userRepository.findByUsername(username);
		return findUser.orElse(null);
	}
	
	// 수정
	public void modify(JoinRequest joinRequest, SiteUser loginUser) {
		joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));
		
		loginUser.setPassword(joinRequest.getPassword());
		loginUser.setTel(joinRequest.getTel());
		loginUser.setModifyDate(LocalDateTime.now());
		
		userRepository.save(loginUser);
	}
	
	public void resign(SiteUser loginUser) {
		loginUser.setDelYn("Y");
		
		userRepository.save(loginUser);
	}
	
}
