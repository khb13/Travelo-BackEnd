package com.mysite.travelo.yeon.user;

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
		
		joinRequest.setPassword1(bCryptPasswordEncoder.encode(joinRequest.getPassword1()));
		
		userRepository.save(joinRequest.toEntity());
	}
	
	public SiteUser login(LoginRequest loginRequest) {
		Optional<SiteUser> findUser = userRepository.findByUsername(loginRequest.getUsername());
		
		if (findUser.get() == null) {
			return null;
		}
		
		if (!findUser.get().getPassword().equals(loginRequest.getPassword())) {
			return null;
		}
		
		return findUser.get();
	}
	
	public SiteUser getLoginUserByUsername(String username) {
		if (username == null) return null;
		
		Optional<SiteUser> findUser = userRepository.findByUsername(username);
		return findUser.orElse(null);
	}
	
}
