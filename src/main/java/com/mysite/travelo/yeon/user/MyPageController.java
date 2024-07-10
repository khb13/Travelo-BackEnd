package com.mysite.travelo.yeon.user;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class MyPageController {
	
	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final TokenBlacklistService tokenBlacklistService;
	
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$");
	
    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(Authentication auth) {
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        return ResponseEntity.ok().body(loginUser);
    }
    
    @PostMapping("/modify")
    public ResponseEntity<String> modify(@RequestParam Map<String, String> map, Authentication auth) {
		
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck")) || !StringUtils.hasText(map.get("tel"))) {
        	return new ResponseEntity<>("모든 필드를 채워주세요", HttpStatus.BAD_REQUEST);
        }
    	
        // 비밀번호 형식 체크
        if (!PASSWORD_PATTERN.matcher(map.get("password")).matches()) {
        	return new ResponseEntity<>("비밀번호는 소문자 영문과 숫자를 포함하여 8자 이상 20자 이하여야 합니다", HttpStatus.BAD_REQUEST);
        }

        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
        	return new ResponseEntity<>("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }
        
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        if (map.get("tel").equals(loginUser.getTel())) {
        	
        	if (bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
        		return new ResponseEntity<>("기존 비밀번호는 사용할 수 없습니다", HttpStatus.BAD_REQUEST);
        	} 
        	
        } 
        
        userService.modify(map, loginUser);
    	
        return new ResponseEntity<>("성공적으로 수정되었습니다", HttpStatus.OK);
    }
	
    @PostMapping("/resign")
    public ResponseEntity<String> resign(@RequestParam Map<String, String> map, Authentication auth, @RequestHeader("Authorization") String accessToken) {
    	
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck"))) {
            return new ResponseEntity<>("모든 필드를 채워주세요", HttpStatus.BAD_REQUEST);
        }
    	
        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
        	return new ResponseEntity<>("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }
    	
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        if (!bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
        	return new ResponseEntity<>("비밀번호가 틀렸습니다", HttpStatus.NOT_FOUND);
        }
        
        userService.resign(loginUser);
        
        if (accessToken.startsWith("Bearer ")) {
        	accessToken = accessToken.substring(7);
        }
        
        tokenBlacklistService.addToken(accessToken);
        
        return new ResponseEntity<>("성공적으로 탈퇴되었습니다", HttpStatus.OK);
    }
    
    @PostMapping("/check")
    public ResponseEntity<String> checkUser(@RequestParam Map<String, String> map, HttpSession session) {
    	
    	// Null 체크
        if (!StringUtils.hasText(map.get("username")) || !StringUtils.hasText(map.get("tel"))) {
            return new ResponseEntity<>("모든 필드를 채워주세요", HttpStatus.BAD_REQUEST);
        }
    	
        SiteUser loginUser = userService.getUser(map.get("username"));
        
        if (loginUser.getUsername().equals(map.get("username")) && loginUser.getTel().equals(map.get("tel"))) {
        	session.setAttribute("username", loginUser.getUsername());
        	return new ResponseEntity<>("user/resetPassword", HttpStatus.OK);
        }
        
        return new ResponseEntity<>("해당하는 정보가 없습니다", HttpStatus.NOT_FOUND);
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam Map<String, String> map, HttpSession session) {
        
    	// Null 체크
        if (!StringUtils.hasText(map.get("password")) || !StringUtils.hasText(map.get("passwordCheck"))) {
        	return new ResponseEntity<>("모든 필드를 채워주세요", HttpStatus.BAD_REQUEST);
        }
    	
        String username = (String)session.getAttribute("username");
    	SiteUser loginUser = userService.getUser(username);
    	
    	if (username == null) {
            return new ResponseEntity<>("세션이 만료되었습니다. 다시 시도해주세요.", HttpStatus.UNAUTHORIZED);
        }
    	
    	// 비밀번호 형식 체크
        if (!PASSWORD_PATTERN.matcher(map.get("password")).matches()) {
        	return new ResponseEntity<>("비밀번호는 소문자 영문과 숫자를 포함하여 8자 이상 20자 이하여야 합니다", HttpStatus.BAD_REQUEST);
        }
    	
    	// 비밀번호 = 비밀번호 체크 여부 확인
        if (!map.get("password").equals(map.get("passwordCheck"))) {
        	return new ResponseEntity<>("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }
        
        if (bCryptPasswordEncoder.matches(map.get("password"), loginUser.getPassword())) {
        	return new ResponseEntity<>("기존 비밀번호는 사용할 수 없습니다", HttpStatus.BAD_REQUEST);
        }
        
    	userService.resetPassword(map, loginUser);
    	session.invalidate();
    	
    	return new ResponseEntity<>("비밀번호 변경되었습니다", HttpStatus.OK);
    }
    
}
