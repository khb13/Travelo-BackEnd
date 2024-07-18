package com.mysite.travelo.yeon.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"})
public class AdminUserController {

	@Autowired
	private UserService userService;
	@Autowired
	
	@GetMapping("/getAllUser")
	public ResponseEntity<?> getAllUser() {
		
		Page<SiteUser> userList =  userService.getAllUsers();
		
		if (userList.isEmpty()) {
			return new ResponseEntity<>("회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(userList);
	}
	
	@GetMapping("/getUsers")
    public ResponseEntity<?> getUsers(@RequestParam(name = "active", defaultValue = "true") boolean active) {
        
		Page<SiteUser> userList = null;
		
		if (active) {
        	userList = userService.getActiveUsers("N");
        } else {
        	userList = userService.getActiveUsers("Y");
        }
		
		if (userList == null) {
			return new ResponseEntity<>("회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(userList);
    }

	@PostMapping("/deleteUser/{userSeq}")
	public ResponseEntity<?> deleteUser(@PathVariable("userSeq") Integer userSeq) {
		/*
		현지 언니 코드에 넣을 거 (블라인드 댓글 개수에 따라 회원 탈퇴시키기)
		SiteUser user = userService.getUser(userSeq);
		List<Review> blindReviews = reviewService.getBlindReview(user);
		
		if (blindReviews.size() >= 5) {
			userService.resign(user);
		}
		
		return "탈퇴시켰습니다.";
		/**/
		
		SiteUser user = userService.getUser(userSeq);
		
		if (user == null) {
			return new ResponseEntity<>("userSeq에 해당하는 회원이 없습니다.", HttpStatus.NOT_FOUND);
		}
		
		userService.resign(user);
		
		return ResponseEntity.ok("탈퇴 시켰습니다.");
	}
	
	@GetMapping("/userDetail/{userSeq}")
	public ResponseEntity<?> userDetail(@PathVariable("userSeq") Integer userSeq) {
		
		SiteUser user = userService.getUser(userSeq);
		
		if (user == null) {
			return new ResponseEntity<>("userSeq에 해당하는 회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(user);
	}
	
}
