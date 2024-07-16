package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	
	public void securityJoin(Map<String, String> map) {
		if (userRepository.existsByUsername(map.get("username"))) {
			return;
		}
		
		SiteUser user = new SiteUser();
		user.setUsername(map.get("username"));
		user.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
		user.setTel(map.get("tel"));
		user.setRole(UserRole.USER);
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		
		userRepository.save(user);
	}
	
	public SiteUser login(LoginRequest loginRequest) {
		Optional<SiteUser> findUser = userRepository.findByUsername(loginRequest.getUsername());
		
		if (findUser.isEmpty()) {
			return null;
		}
		
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), findUser.get().getPassword())) {
			return null;
		}
		
		return findUser.get();
	}
	
	public SiteUser getLoginUserByUsername(String username) {
		if (username == null) {
			return null;
		}
		
		Optional<SiteUser> findUser = userRepository.findByUsername(username);
		return findUser.orElse(null);
	}
	
	public SiteUser getUser(String username) {
		if (username == null) {
			return null;
		}
		
		Optional<SiteUser> findUser = userRepository.findByUsername(username);
		
		if (findUser.isEmpty()) {
			return null;
		}

		return findUser.get();
	}
	
	public SiteUser getUser(Integer userSeq) {
		if (userSeq == null) {
			return null;
		}
		
		Optional<SiteUser> findUser = userRepository.findById(userSeq);
		
		if (findUser.isEmpty()) {
			return null;
		}
		
		return findUser.get();
	}
	
	// 수정
	public void modify(Map<String, String> map, SiteUser loginUser) {
		loginUser.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
		loginUser.setTel(map.get("tel"));
		loginUser.setModifyDate(LocalDateTime.now());
		
		userRepository.save(loginUser);
	}
	
	// 탈퇴
	public void resign(SiteUser loginUser) {
		loginUser.setDelYn("Y");
		
		userRepository.save(loginUser);
	}
	
	// 비밀번호 재설정
	public void resetPassword(Map<String, String> map, SiteUser loginUser) {
		loginUser.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
		
		userRepository.save(loginUser);
	}
	
	// 전체 회원 목록
	public Page<SiteUser> getAllUsers() {
        Pageable pageable = PageRequest.of(0, 15); 
        
        return userRepository.findByRole(pageable, UserRole.USER);
    }
	
	// 탈퇴 여부에 따른 회원 목록
	public Page<SiteUser> getActiveUsers(String delYn) {
		Pageable pageable = PageRequest.of(0, 15);
		
		return userRepository.findByRoleAndDelYn(pageable, UserRole.USER, delYn);
	}
	
}
