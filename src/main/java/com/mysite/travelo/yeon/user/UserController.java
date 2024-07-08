package com.mysite.travelo.yeon.user;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JWTUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;
	
	@PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest,
                       BindingResult bindingResult) {

		// 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 클라이언트에 반환
            return bindingResult.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        }
		
        // 이메일 중복 여부 확인
        if (userService.checkUsernameDuplicate(joinRequest.getUsername())) {
            return "이미 가입된 이메일입니다";
        }

        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다";
        }

        // 에러가 존재하지 않을 시 joinRequest 통해서 회원가입 완료
        userService.securityJoin(joinRequest);

        // 회원가입 시 홈 화면으로 이동
        return "redirect:/user/login";
    }
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        SiteUser user = userService.login(loginRequest);

        if (user == null) {
            return ResponseEntity.ok("이메일 또는 비밀번호가 일치하지 않습니다");
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
            throw new RuntimeException("Refresh token is expired");
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
    public String logout(@RequestHeader("Authorization") String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
        	accessToken = accessToken.substring(7);
        }
        
        tokenBlacklistService.addToken(accessToken);
        
        return "redirect:/";
    }
    
}