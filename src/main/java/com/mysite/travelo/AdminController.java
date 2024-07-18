package com.mysite.travelo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.gil.review.Review;
import com.mysite.travelo.gil.review.ReviewService;
import com.mysite.travelo.yeon.group.CourseGroup;
import com.mysite.travelo.yeon.group.CourseGroupService;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final CourseService courseService;
	private final ReviewService reviewService;
	private final CourseGroupService courseGroupService;
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/main")
    public ResponseEntity<Map<String, Object>> adminMain() {
		
		Map<String, Object> response = new HashMap<>(); 
		
		// 전체 코스 개수
		List<Course> courses = courseService.getAllCourseCount();
		response.put("courseCnt", courses.size());
		
		// 전체 후기 개수
		List<Review> reviews = reviewService.getAllReviewCount();
		response.put("reviewCnt", reviews.size());
		
		// 신고 수 5개 이상인 후기 개수
		List<Review> reportReviews = reviewService.getReportReviewCount();
		response.put("blindReviewCnt", reportReviews.size());
		
		// 전체 그룹 개수
		List<CourseGroup> groups = courseGroupService.getAllGroupCount();
		response.put("groupCnt", groups.size());
		
        return ResponseEntity.ok(response);
    }
	
	// 전체 코스
	@GetMapping("/courses")
	public ResponseEntity<?> adminCourses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<Course> courses = courseService.getAllCourse(page, sortBy);
		
		if (courses == null) {
			return new ResponseEntity<>("코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(courses);
	}
	
	// private 여부에 따른 전체 코스
	@GetMapping("/getVisibleCourses")
	public ResponseEntity<?> adminCourses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(value = "privateYn", defaultValue = "N") String privateYn,
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<Course> courses = courseService.getVisibleCourses(page, privateYn, sortBy);
		
		if (courses == null) {
			return new ResponseEntity<>("코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(courses);
	}
	
	// 사용자에 따른 코스
	@GetMapping("/courses/{userSeq}")
	public ResponseEntity<?> adminCoursesByUser(@PathVariable("userSeq") Integer userSeq, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		SiteUser author = userService.getUser(userSeq);
		
		Page<Course> courses = courseService.getAllCourseByUser(author, page, sortBy);
		
		if (courses == null) {
			return new ResponseEntity<>("코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(courses);
	}
	
	
	@GetMapping("/reviews")
	public ResponseEntity<?> adminReviews(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
	
		Page<Review> reviews = reviewService.getAllReview(page, sortBy);
		
		if (reviews == null) {
			return new ResponseEntity<>("후기가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(reviews);
	}
	
	@GetMapping("/getBlindReviews")
	public ResponseEntity<?> getBlindReviews(@RequestParam(defaultValue = "0") int page,
			@RequestParam(value = "blindYn", defaultValue = "N") String blindYn,
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
	
		Page<Review> reviews = reviewService.getBlindReviews(page, blindYn, sortBy);
		
		if (reviews == null) {
			return new ResponseEntity<>("후기가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(reviews);
	}
	
	@GetMapping("/reviews/{userSeq}")
	public ResponseEntity<?> adminReviewsByUser(@PathVariable("userSeq") Integer userSeq, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<Review> reviews = reviewService.getAllReviewByUser(userSeq, page, sortBy);
		
		if (reviews == null) {
			return new ResponseEntity<>("후기가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(reviews);
	}
	
	@GetMapping("/groups")
	public ResponseEntity<?> adminGroups(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<CourseGroup> groups = courseGroupService.getAllGroup(page, sortBy);
		
		return ResponseEntity.ok(groups);
	}
	
	@GetMapping("/groups/{userSeq}")
	public String adminGroupsByUser(@PathVariable("userSeq") Integer userSeq,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		SiteUser user = userService.getUser(userSeq);
		
		Page<CourseGroup> groups = courseGroupService.getAllGroupByUser(user.getUsername(), page, sortBy);
		
		return new String();
	}
	
	
}
