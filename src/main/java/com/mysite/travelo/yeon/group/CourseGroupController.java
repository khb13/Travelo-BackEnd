package com.mysite.travelo.yeon.group;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseBookmark;
import com.mysite.travelo.gil.course.CourseBookmarkService;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/group")
public class CourseGroupController {

	private final UserService userService;
	private final CourseGroupService courseGroupService;
	private final CourseBookmarkService bookmarkService;
	private final CourseService courseService;
	private final CourseGroupListService courseGroupListService;
	
	// 코스 그룹 리스트
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public ResponseEntity<?> list(Authentication auth) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		List<CourseGroup> courseGroup = courseGroupService.getList(loginUser);
		
		if (courseGroup == null || courseGroup.isEmpty()) {
			return new ResponseEntity<>("그룹이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("courseGroup", courseGroup);
        
		return ResponseEntity.ok(response);
	}
	
	// 코스 그룹 리스트의 상세 보기(그룹에 담긴 코스 목록을 보여줌) - 수정 페이지에서도 쓰임
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail/{courseGroupSeq}")
	public ResponseEntity<?> detail(Authentication auth, @PathVariable("courseGroupSeq") Integer courseGroupSeq) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		CourseGroup courseGroup = courseGroupService.getCourse(courseGroupSeq);
		
		if (courseGroup == null) {
			return new ResponseEntity<>("코스 목록이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> response = new HashMap<>();
        response.put("loginUser", loginUser);
        response.put("courseGroup", courseGroup);

        return ResponseEntity.ok(response);
	}
	
	// 코스 그룹 삭제
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete/{courseGroupSeq}")
	public ResponseEntity<String> delete(@PathVariable("courseGroupSeq") Integer courseGroupSeq) {
		
		CourseGroup courseGroup = courseGroupService.getCourse(courseGroupSeq);
		
		if (courseGroup == null) {
			return new ResponseEntity<>("코스 목록이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		courseGroupService.delete(courseGroup);
		
		return ResponseEntity.ok("그룹 삭제되었습니다");
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public ResponseEntity<?> create(Authentication auth, @Valid @RequestBody CourseGroupRequest courseGroupRequest, BindingResult bindingResult) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		Map<String, Object> response = new HashMap<>();
		
		if (bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
		
		if (courseGroupRequest.getCourseSeqs().size() > 4) {
			return new ResponseEntity<>("코스는 최대 4개까지 추가할 수 있습니다", HttpStatus.BAD_REQUEST);
		}
		
		boolean exist = courseService.existCourse(courseGroupRequest.getCourseSeqs());
		
		if (exist) {
			CourseGroup courseGroup = courseGroupService.create(courseGroupRequest, loginUser);
			courseGroupListService.create(courseGroup, courseGroupRequest.getCourseSeqs());
			
			return ResponseEntity.ok("그룹이 등록되었습니다");
		}
		
		return new ResponseEntity<>("존재하지 않는 코스를 저장하려고 시도했습니다", HttpStatus.BAD_REQUEST);
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{courseGroupSeq}")
	public ResponseEntity<?> modify(Authentication auth, @Valid @RequestBody CourseGroupRequest courseGroupRequest
			, BindingResult bindingResult, @PathVariable("courseGroupSeq") Integer courseGroupSeq) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		Map<String, Object> response = new HashMap<>();
		
		if (bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
		
		if (courseGroupRequest.getCourseSeqs().size() > 4) {
			return new ResponseEntity<>("코스는 최대 4개까지 추가할 수 있습니다", HttpStatus.BAD_REQUEST);
		}
		
		boolean exist = courseService.existCourse(courseGroupRequest.getCourseSeqs());
		String oldTitle = courseGroupService.getCourse(courseGroupSeq).getTitle();
		
		if (exist) {
			
			CourseGroup courseGroup = courseGroupService.modify(courseGroupRequest, loginUser, courseGroupSeq);
			
			if (courseGroup == null) {
				return new ResponseEntity<>("해당하는 코스 그룹이 없습니다", HttpStatus.BAD_REQUEST);
			}

			boolean same = courseGroupListService.modify(courseGroup, courseGroupRequest.getCourseSeqs());
			
			if (oldTitle.equals(courseGroupRequest.getTitle())) {
				
				if (same) {
					return new ResponseEntity<>("변경 사항이 없습니다", HttpStatus.BAD_REQUEST);
				}
			}
			
			return ResponseEntity.ok("그룹이 수정되었습니다");
		}	
		
		return new ResponseEntity<>("존재하지 않는 코스를 저장하려고 시도했습니다", HttpStatus.BAD_REQUEST);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/bookmarks")
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
	@GetMapping("/bookmarks/{areaCode}")
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
	
}
