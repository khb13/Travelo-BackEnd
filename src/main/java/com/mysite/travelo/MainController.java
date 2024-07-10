package com.mysite.travelo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/")
	public ResponseEntity<String> index() {
		return ResponseEntity.ok("hello");
	}
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/admin")
    public String adminPage(Model model) {
		
        return "인가 성공!";
    }
	
}
