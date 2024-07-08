package com.mysite.travelo.yeon.user;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class MyPageController {
	
	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(JoinRequest joinRequest) {
    	
    	// 로그인 프론트까지 완성되면 교체할 부분~~ 매개변수에 Authentication auth 넣어야 
    	SiteUser loginUser = userService.getLoginUserByUsername("hong1@gmail.com");
    	
        return ResponseEntity.ok().body(loginUser);
    }
    
    @PostMapping("/modify")
    public String modify(@Valid JoinRequest joinRequest, BindingResult bindingResult) {
    	
    	// 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 클라이언트에 반환
            return bindingResult.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        }
		
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다";
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername("hong1@gmail.com");
        
        if (bCryptPasswordEncoder.matches(joinRequest.getPassword(), loginUser.getPassword())) {
            return "예전 비밀번호는 사용할 수 없습니다";
        }
        
        userService.modify(joinRequest, loginUser);
    	
        return "yeon/mypage";
    }
	
    @PostMapping("/resign")
    public String resign(@Valid ResignRequest resignRequest, BindingResult bindingResult) {
    	
    	// 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 클라이언트에 반환
            return bindingResult.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        }
		
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!resignRequest.getPassword().equals(resignRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다";
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername("hong1@gmail.com");
    	
        if (!bCryptPasswordEncoder.matches(resignRequest.getPassword(), loginUser.getPassword())) {
            return "비밀번호가 틀렸습니다";
        }
        
        userService.resign(loginUser);
        
        return "탈퇴 되었습니다.";
    }
    
    @PostMapping("/check")
    public String postMethodName(CheckRequest checkRequest) {
    	
        return "";
    }
    
    
}
