package com.mysite.travelo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.travelo.yeon.user.CheckRequest;
import com.mysite.travelo.yeon.user.JoinRequest;
import com.mysite.travelo.yeon.user.LoginRequest;
import com.mysite.travelo.yeon.user.ResignRequest;

@Controller
public class MainController {

	@GetMapping("/")
	public ResponseEntity<String> index() {
		return ResponseEntity.ok("hello");
	}
	
	@GetMapping("/user/join")
    public String join(JoinRequest joinRequest) {

        return "yeon/join/form";
    }
	
	@GetMapping("/user/login")
	public String login(LoginRequest loginRequest) {
		return "yeon/login/form";
	} 
	
	@GetMapping("/user/check")
	public String check(CheckRequest checkRequest) {
		return "yeon/checkUser";
	}
	
	// GetMapping html 파일을 새로 만들텐데 어떻게... 보낼 건지? 확인 후 수정 예정
	@GetMapping("/user/resetPassword")
	public String resetPassword(ResignRequest resignRequest) {
		return "yeon/";
	}
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/admin")
    public String adminPage(Model model) {
		
        return "인가 성공!";
    }
	
}
