package com.mysite.travelo.yeon.user;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JWTUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[a-z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$");
	
	@PostMapping("/join")
    public ResponseEntity<String> join(@RequestParam Map<String, String> map) {
		
		// Null 체크
        if (!StringUtils.hasText(map.get("username")) || !StringUtils.hasText(map.get("password")) || 
            !StringUtils.hasText(map.get("passwordCheck")) || !StringUtils.hasText(map.get("tel"))) {
        	return new ResponseEntity<>("모든 필드를 채워주세요", HttpStatus.BAD_REQUEST);
        }
		
        // 이메일 형식 체크
        if (!EMAIL_PATTERN.matcher(map.get("username")).matches()) {
        	return new ResponseEntity<>("이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST);
        }
        
        // 이메일 중복 여부 확인
        if (userService.checkUsernameDuplicate(map.get("username"))) {
        	return new ResponseEntity<>("이미 가입된 이메일입니다", HttpStatus.BAD_REQUEST);
        }
        
        // 비밀번호 형식 체크
        if (!PASSWORD_PATTERN.matcher(map.get("password")).matches()) {
        	return new ResponseEntity<>("비밀번호는 소문자 영문과 숫자를 포함하여 8자 이상 20자 이하여야 합니다", HttpStatus.BAD_REQUEST);
        }

        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
        	return new ResponseEntity<>("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }

        userService.securityJoin(map);

        return ResponseEntity.ok("가입 되었습니다");
    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

		SiteUser user = userService.login(loginRequest);

        if (user == null) {
            return new ResponseEntity<>("이메일 또는 비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtUtil.createJwt(user.getUsername(), user.getRole().toString(), 1000 * 60 * 60L);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().toString(), 1000 * 60 * 60 * 24 * 7);
        
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);

        return ResponseEntity.ok().body(authResponse);
    }
	
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (jwtUtil.isExpired(refreshToken)) {
            throw new RuntimeException("Refresh token이 만료되었습니다");
        }

        String username = jwtUtil.getUsername(refreshToken);
        SiteUser user = userService.getLoginUserByUsername(username);

        String accessToken = jwtUtil.createJwt(user.getUsername(), user.getRole().toString(), 1000 * 60 * 60L);

        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);

        return ResponseEntity.ok().body(authResponse);
    }

    // 토큰 값 받아와서 사용자 정보 추출
	@GetMapping("/info")
    public String memberInfo(Authentication auth) {
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());

        return "email : " + loginUser.getUsername() + "\nrole : " + loginUser.getRole();
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
        	accessToken = accessToken.substring(7);
        }
        
        tokenBlacklistService.addToken(accessToken);
        
        return ResponseEntity.ok("로그아웃 되었습니다");
    }
    
}