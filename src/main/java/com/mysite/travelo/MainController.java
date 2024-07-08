package com.mysite.travelo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.travelo.yeon.user.JoinRequest;
import com.mysite.travelo.yeon.user.LoginRequest;

@Controller
public class MainController {

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/")
	public String index() {
		return "hello";
	}
	
	@GetMapping("/user/join")
    public String joinPage(JoinRequest joinRequest) {

        return "yeon/join/form";
    }
	
	@GetMapping("user/login")
	public String login(LoginRequest loginRequest) {
		return "yeon/login/form";
	} 
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/admin")
    public String adminPage(Model model) {

        return "인가 성공!";
    }
	
}
