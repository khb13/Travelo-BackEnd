package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public User getUser(String username) {
		
		Optional<User> user = this.userRepository.findByUsername(username);
		
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
		
	}
	
	public void create(Map<String, String> map) {
		
		User user = new User();
		user.setUsername(map.get("username"));
		user.setPassword(passwordEncoder.encode(map.get("password")));
		user.setTel(map.get("tel"));
		user.setRegisterDate(LocalDateTime.now());
		user.setDelYn("N");
		this.userRepository.save(user);
		
	}
	
}
