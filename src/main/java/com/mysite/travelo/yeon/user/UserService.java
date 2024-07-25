package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	
	public void joinMail(Map<String, String> map) {
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
	
	public void joinKakao(String username, String id) {
		if (userRepository.existsByUsername(username)) {
			return;
		}
		
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setPassword(bCryptPasswordEncoder.encode("1111"));
		user.setRole(UserRole.USER);
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		user.setOauthType("kakao");
		user.setOauthId(id);
		
		userRepository.save(user);
	}
	
	public void joinGoogle(String username) {
		if (userRepository.existsByUsername(username)) {
			return;
		}
		
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setPassword(bCryptPasswordEncoder.encode("1111"));
		user.setRole(UserRole.USER);
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		user.setOauthType("google");
		
		userRepository.save(user);
	}
	
	public void joinNaver(String username, String tel) {
		if (userRepository.existsByUsername(username)) {
			return;
		}
		
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setPassword(bCryptPasswordEncoder.encode("1111"));
		user.setTel(tel);
		user.setRole(UserRole.USER);
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		user.setOauthType("naver");
		
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
		
		if (findUser.get().getDelYn().equals("Y")) {
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
		loginUser.setOauthId(map.get("oauthId"));
		
		userRepository.save(loginUser);
	}
	
	public void modifyOauth(Map<String, String> map, SiteUser user) {
		
		user.setOauthType(map.get("oauthType"));
		user.setOauthId(map.get("oauthId"));
		user.setModifyDate(LocalDateTime.now());
		
		userRepository.save(user);
	}
	
	// 탈퇴
	public void resign(SiteUser loginUser) {
		loginUser.setDelYn("Y");
		loginUser.setResignDate(LocalDateTime.now()); 
		
		userRepository.save(loginUser);
	}
	
	// 비밀번호 재설정
	public void resetPassword(Map<String, String> map, SiteUser loginUser) {
		loginUser.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
		
		userRepository.save(loginUser);
	}
	
	// 전체 회원 목록
	public Page<SiteUser> getAllUsers(int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 15); 
        
        if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "registerDate")); // 최신순
		} else if ("oldest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "registerDate")); // 오래된 순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "registerDate")); // 최신순(디폴트)
		}
        
        return userRepository.findByRole(pageable, UserRole.USER);
    }
	
	// 탈퇴 여부에 따른 회원 목록
	public Page<SiteUser> getActiveUsers(int page, String sortBy, String delYn) {
		Pageable pageable = PageRequest.of(page, 15);
		
		if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "registerDate")); // 최신순
		} else if ("oldest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "registerDate")); // 오래된 순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "registerDate")); // 최신순(디폴트)
		}
		
		return userRepository.findByRoleAndDelYn(pageable, UserRole.USER, delYn);
	}
	
	public List<SiteUser> getAllUsersCount() {
		
		return userRepository.findAll();
	}
	
}
