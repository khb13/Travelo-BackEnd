package com.mysite.travelo.yeon.user;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@Controller
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
    public String login(@RequestBody LoginRequest loginRequest){

        SiteUser user = userService.login(loginRequest);

        if(user == null){
            return "이메일 또는 비밀번호가 일치하지 않습니다";
        }

        String token = jwtUtil.createJwt(user.getUsername(), user.getRole().toString(), 1000 * 60 * 60L);
        return token;
    }

	@GetMapping("/info")
    public String memberInfo(Authentication auth) {

		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());

        return "ID : " + loginUser.getUsername() + "\nrole : " + loginUser.getRole();
    }
    
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenBlacklistService.addToken(token);
        return "로그아웃 되었습니다";
    }
	
}