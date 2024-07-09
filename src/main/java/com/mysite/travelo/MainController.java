package com.mysite.travelo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.travelo.yeon.group.Course;
import com.mysite.travelo.yeon.group.CourseBookmark;
import com.mysite.travelo.yeon.group.CourseBookmarkService;
import com.mysite.travelo.yeon.group.CourseGroupService;
import com.mysite.travelo.yeon.group.CourseService;
import com.mysite.travelo.yeon.user.CheckRequest;
import com.mysite.travelo.yeon.user.JoinRequest;
import com.mysite.travelo.yeon.user.LoginRequest;
import com.mysite.travelo.yeon.user.ResignRequest;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
public class MainController {

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
