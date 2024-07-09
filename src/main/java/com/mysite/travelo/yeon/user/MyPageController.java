package com.mysite.travelo.yeon.user;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class MyPageController {
	
	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$");
	
    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(Authentication auth) {
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        return ResponseEntity.ok().body(loginUser);
    }
    
    @PostMapping("/modify")
    public String modify(@RequestParam Map<String, String> map, Authentication auth) {
		
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck")) || !StringUtils.hasText(map.get("tel"))) {
            return "모든 필드를 채워주세요";
        }
    	
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
            return "비밀번호가 일치하지 않습니다";
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        if (bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
            return "기존 비밀번호는 사용할 수 없습니다";
        }
        
        // 비밀번호 형식 체크
        if (!PASSWORD_PATTERN.matcher(map.get("password")).matches()) {
            return "비밀번호는 소문자 영문과 숫자를 포함하여 8자 이상 20자 이하여야 합니다";
        }
        
        userService.modify(map, loginUser);
    	
        return "yeon/mypage";
    }
	
    @PostMapping("/resign")
    public String resign(@RequestParam Map<String, String> map, Authentication auth) {
    	
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck"))) {
            return "모든 필드를 채워주세요";
        }
    	
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
            return "비밀번호가 일치하지 않습니다";
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        if (!bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
            return "비밀번호가 틀렸습니다";
        }
        
        userService.resign(loginUser);
        
        return "redirect:/";
    }
    
    @PostMapping("/check")
    public String checkUser(@RequestParam Map<String, String> map, Authentication auth) {
    	
    	// Null 체크
        if (!StringUtils.hasText(map.get("username")) || !StringUtils.hasText(map.get("tel"))) {
            return "모든 필드를 채워주세요";
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        if (loginUser.getUsername().equals(map.get("username")) && loginUser.getTel().equals(map.get("tel"))) {
        	return "user/resetPassword";
        }
        
        return "해당하는 정보가 없습니다";
    }
    
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam Map<String, String> map, Authentication auth) {
        
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck"))) {
            return "모든 필드를 채워주세요";
        }
    	
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
    	// 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
            return "비밀번호가 일치하지 않습니다";
        }
        
        if (bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
            return "기존 비밀번호는 사용할 수 없습니다";
        }
        
        // 비밀번호 형식 체크
        if (!PASSWORD_PATTERN.matcher(map.get("password")).matches()) {
            return "비밀번호는 소문자 영문과 숫자를 포함하여 8자 이상 20자 이하여야 합니다";
        }
    	
    	userService.resetPassword(map, loginUser);
    	
        return "redirect:/user/login";
    }
    
}
