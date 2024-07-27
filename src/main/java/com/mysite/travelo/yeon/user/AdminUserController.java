package com.mysite.travelo.yeon.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

	@Autowired
	private UserService userService;
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getAllUser")
	public ResponseEntity<?> getAllUser(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<SiteUser> userList =  userService.getAllUsers(page, sortBy);
		
		if (userList.isEmpty()) {
			return new ResponseEntity<>("회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(userList);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getUsers")
    public ResponseEntity<?> getUsers(@RequestParam(name = "page", defaultValue = "0") int page,
    		@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy,
    		@RequestParam(name = "active", defaultValue = "true") boolean active) {
        
		Page<SiteUser> userList = null;
		
		if (active) {
        	userList = userService.getActiveUsers(page, sortBy, "N");
        } else {
        	userList = userService.getActiveUsers(page, sortBy, "Y");
        }
		
		if (userList == null) {
			return new ResponseEntity<>("회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(userList);
    }

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/deleteUser/{userSeq}")
	public ResponseEntity<?> deleteUser(@PathVariable("userSeq") Integer userSeq) {
		
		SiteUser user = userService.getUser(userSeq);
		
		if (user == null) {
			return new ResponseEntity<>("userSeq에 해당하는 회원이 없습니다.", HttpStatus.NOT_FOUND);
		}
		
		userService.resign(user);
		
		return ResponseEntity.ok("탈퇴 시켰습니다.");
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/deleteUsers")
	public ResponseEntity<?> deleteUsers(@RequestBody Map<String, List<Integer>> map) {
		
		if (map.get("userSeqs").size() == 0 ) {
			return new ResponseEntity<>("탈퇴 시킬 회원이 없습니다", HttpStatus.BAD_REQUEST);
		}
		
		List<Integer> seqs = map.get("userSeqs");
		List<Integer> userSeqs = new ArrayList<>();
		
		for (Integer seq : seqs) {
			SiteUser user = userService.getUser(seq);
			
			if (user == null) {
				return new ResponseEntity<>("존재하지 않는 회원을 탈퇴 시키려고 시도했습니다.", HttpStatus.NOT_FOUND);
			}
			
			userSeqs.add(seq);
		}
		
		for (Integer userSeq : userSeqs) {
			SiteUser user = userService.getUser(userSeq);
			userService.resign(user);
		}
		
		return ResponseEntity.ok("탈퇴 시켰습니다.");
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/userDetail/{userSeq}")
	public ResponseEntity<?> userDetail(@PathVariable("userSeq") Integer userSeq) {
		
		SiteUser user = userService.getUser(userSeq);
		
		if (user == null) {
			return new ResponseEntity<>("userSeq에 해당하는 회원이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(user);
	}
	
}
