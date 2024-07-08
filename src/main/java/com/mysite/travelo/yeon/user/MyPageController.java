package com.mysite.travelo.yeon.user;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> myPage(JoinRequest joinRequest, Authentication auth) {
    	
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        return ResponseEntity.ok().body(loginUser);
    }
    
    @PostMapping("/modify")
    public String modify(@Valid JoinRequest joinRequest, BindingResult bindingResult, Authentication auth) {
    	
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
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        if (bCryptPasswordEncoder.matches(joinRequest.getPassword(), loginUser.getPassword())) {
            return "기존 비밀번호는 사용할 수 없습니다";
        }
        
        userService.modify(joinRequest, loginUser);
    	
        return "yeon/mypage";
    }
	
    @PostMapping("/resign")
    public String resign(@Valid ResignRequest resignRequest, BindingResult bindingResult, Authentication auth) {
    	
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
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        if (!bCryptPasswordEncoder.matches(resignRequest.getPassword(), loginUser.getPassword())) {
            return "비밀번호가 틀렸습니다";
        }
        
        userService.resign(loginUser);
        
        return "redirect:/";
    }
    
    @PostMapping("/check")
    public String checkUser(@Valid CheckRequest checkRequest, BindingResult bindingResult, Authentication auth) {
    	
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        if (loginUser.getUsername().equals(checkRequest.getUsername()) && loginUser.getTel().equals(checkRequest.getTel())) {
        	return "user/resetPassword";
        }
        
        return "해당하는 정보가 없습니다";
    }
    
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid ResignRequest resignRequest, BindingResult bindingResult, Authentication auth) {
        
    	if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.joining(", "));
        }
    	
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
    	// 비밀번호 = 비밀번호 체크 여부 확인
        if (!resignRequest.getPassword().equals(resignRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다";
        }
        
        if (bCryptPasswordEncoder.matches(resignRequest.getPassword(), loginUser.getPassword())) {
            return "기존 비밀번호는 사용할 수 없습니다";
        }
    	
    	userService.resetPassword(resignRequest, loginUser);
    	
        return "redirect:/user/login";
    }
    
    
    
}
