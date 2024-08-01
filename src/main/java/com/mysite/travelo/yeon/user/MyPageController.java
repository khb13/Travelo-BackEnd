package com.mysite.travelo.yeon.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseBookmark;
import com.mysite.travelo.gil.course.CourseBookmarkService;
import com.mysite.travelo.gil.course.CourseLikeService;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.yeon.group.CourseGroupListService;
import com.mysite.travelo.yeon.group.CourseGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MyPageController {
	
	private final UserService userService;
	private final CourseService courseService;
	private final CourseLikeService courseLikeService;
	private final CourseBookmarkService courseBookmarkService;
	private final CourseGroupListService courseGroupListService;
	private final CourseBookmarkService bookmarkService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final TokenBlacklistService tokenBlacklistService;
	
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$");
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(Authentication auth) {
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        return ResponseEntity.ok().body(loginUser);
    }
    
	@PreAuthorize("isAuthenticated()")
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
    	
        return ResponseEntity.ok("성공적으로 수정되었습니다");
    }
	
	@PreAuthorize("isAuthenticated()")
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
        
        return ResponseEntity.ok("성공적으로 탈퇴되었습니다");
    }
    
    @PreAuthorize("isAuthenticated()")
	@GetMapping("/courseBookmarks")
	public ResponseEntity<?> bookmarks(Authentication auth) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseBookmark> bookmarks = bookmarkService.getList(loginUser.getUserSeq());
		
		if (bookmarks == null) {
			return new ResponseEntity<>("북마크 한 코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("bookmarks", bookmarks);
        
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/courseBookmarks/{areaCode}")
	public ResponseEntity<?> bookmarksByArea(Authentication auth, @PathVariable("areaCode") String areaCode) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseBookmark> bookmarks = bookmarkService.getListByArea(loginUser.getUserSeq(), areaCode);
		
		if (bookmarks == null) {
			return new ResponseEntity<>("지역 코드 값에 해당하는 북마크가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("bookmarks", bookmarks);
        
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/customCourses")
	public ResponseEntity<?> customCourses(Authentication auth) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<Course> customCourses = courseService.getCustom(loginUser);
		
		if (customCourses == null) {
			return new ResponseEntity<>("커스텀 한 코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("customCourses", customCourses);
        
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/customCourses/{areaCode}")
	public ResponseEntity<?> customCoursesByArea(Authentication auth, @PathVariable("areaCode") String areaCode) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<Course> customCourses = courseService.getCustomByArea(loginUser, areaCode);
		
		if (customCourses == null) {
			return new ResponseEntity<>("지역 코드 값에 해당하는 커스텀 코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("customCourses", customCourses);

        return ResponseEntity.ok(response);
	}
    
	// 코스 삭제 : 여러 개
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/deleteCourses")
	public ResponseEntity<?> deleteCourses(@RequestBody Map<String, List<Integer>> map) {
		
		if (map.get("courseSeqs").size() == 0 ) {
			return new ResponseEntity<>("삭제 시킬 코스가 없습니다", HttpStatus.BAD_REQUEST);
		}
		
		List<Integer> seqs = map.get("courseSeqs");
		List<Integer> courseSeqs = new ArrayList<>();
		
		for (Integer seq : seqs) {
			Course course = courseService.getCourse(seq);
			
			if (course == null) {
				return new ResponseEntity<>("존재하지 않는 코스를 삭제 시키려고 시도했습니다.", HttpStatus.NOT_FOUND);
			}
			
			courseSeqs.add(seq);
		}
		
		for (Integer courseSeq : courseSeqs) {
			Course course = courseService.getCourse(courseSeq);
			
			courseLikeService.delete(course);
			courseBookmarkService.delete(course);
			courseGroupListService.delete(course);
			courseService.delete(course);
		}
		
		return ResponseEntity.ok("삭제 시켰습니다.");
	}
		
}
