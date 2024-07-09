package com.mysite.travelo.yeon.group;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/group")
@RestController
@RequiredArgsConstructor
public class CourseGroupController {

	private final UserService userService;
	private final CourseGroupService courseGroupService;
	private final CourseBookmarkService bookmarkService;
	private final CourseService courseService;
	
	// 코스 그룹 리스트
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public ResponseEntity<?> list(Authentication auth) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseGroup> courseGroup = courseGroupService.getList(loginUser);
		
		if (courseGroup == null || courseGroup.isEmpty()) {
			return ResponseEntity.ok("그룹이 없습니다");
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("courseGroup", courseGroup);
        
		return ResponseEntity.ok(response);
	}
	
	// 코스 그룹 리스트의 상세 보기(그룹에 담긴 코스 목록을 보여줌)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail/{courseGroupSeq}")
	public ResponseEntity<?> detail(Authentication auth, @PathVariable("courseGroupSeq") Integer courseGroupSeq) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		CourseGroup courseGroup = courseGroupService.getCourse(courseGroupSeq);
		
		if (courseGroup == null) {
			return ResponseEntity.ok("코스 목록이 없습니다");
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("courseGroup", courseGroup);

        return ResponseEntity.ok(response);
	}
	
	// 코스 그룹 삭제
	@GetMapping("/delete/{courseGroupSeq}")
	public String delete(@PathVariable("courseGroupSeq") Integer courseGroupSeq) {
		
		CourseGroup courseGroup = courseGroupService.getCourse(courseGroupSeq);
		
		if (courseGroup == null) {
			return "코스 목록이 없습니다";
		}
		
		courseGroupService.delete(courseGroup);
		
		return "redirect:/group/list";
	}
	
	@PostMapping("/create")
	public String create() {
		
		return "";
	}
	
	@GetMapping("/courseAll")
	public ResponseEntity<?> courseAll(Authentication auth) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseBookmark> bookmarks = bookmarkService.getList(loginUser.getUserSeq());
		List<Course> courses = courseService.getCustom(loginUser);
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("bookmarks", bookmarks);
        response.put("courses", courses);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/courseAllByArea/{areaCode}")
	public ResponseEntity<?> courseAllByArea(Authentication auth, @PathVariable("areaCode") String areaCode) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseBookmark> bookmarks = bookmarkService.getListByArea(loginUser.getUserSeq(), areaCode);
		List<Course> courses = courseService.getCustomByArea(loginUser, areaCode);
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("bookmarks", bookmarks);
        response.put("courses", courses);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/selectCourse")
	public String selectCourse() {
		
		return "";
	}
	
}
