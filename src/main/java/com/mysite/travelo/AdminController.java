package com.mysite.travelo;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		
		// 전체 회원 수(탈퇴 회원 제외)
		List<SiteUser> users = userService.getAllUsersCount();
		response.put("user count", users.size());
		
		// 전체 코스 개수
		List<Course> courses = courseService.getAllCourseCount();
		response.put("course count", courses.size());
		
		// 전체 후기 개수(블라인드 후기 제외)
		List<Review> reviews = reviewService.getAllReviewCount();
		response.put("review count", reviews.size());
		
		// 신고 수 5개 이상인 후기 개수
		List<Review> reportReviews = reviewService.getReportReviewCount();
		response.put("reported5plus count", reportReviews.size());
		
		// 전체 그룹 개수
		List<CourseGroup> groups = courseGroupService.getAllGroupCount();
		response.put("group count", groups.size());
		
        return ResponseEntity.ok(response);
    }
	
	// 전체 코스
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	
	// 전체 후기
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/reviews")
	public ResponseEntity<?> adminReviews(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
	
		Page<Map<String, Object>> reviews = reviewService.getAllReview(page, sortBy);
		
		if (reviews == null) {
			return new ResponseEntity<>("후기가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(reviews);
	}
	
	// blind 여부에 따른 후기
	@PreAuthorize("isAuthenticated()")
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
	
	// 회원 별 후기 목록
	@PreAuthorize("isAuthenticated()")
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
	
	// 전체 그룹 목록
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/groups")
	public ResponseEntity<?> adminGroups(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Page<CourseGroup> groups = courseGroupService.getAllGroup(page, sortBy);
		
		if (groups == null) {
			return new ResponseEntity<>("그룹이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(groups);
	}
	
	// 회원 별 그룹 목록
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/groups/{userSeq}")
	public ResponseEntity<?> adminGroupsByUser(@PathVariable("userSeq") Integer userSeq,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		SiteUser user = userService.getUser(userSeq);
		
		Page<CourseGroup> groups = courseGroupService.getAllGroupByUser(user.getUsername(), page, sortBy);
		
		if (groups == null) {
			return new ResponseEntity<>("그룹이 없습니다", HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(groups);
	}
	
	// 코스 삭제 : 1개
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/deleteCourse/{courseSeq}")
	public ResponseEntity<?> deleteCourse(@PathVariable("courseSeq") Integer courseSeq) {
		
		Course course = courseService.getCourse(courseSeq);
		
		if (course == null) {
			return new ResponseEntity<>("courseSeq에 해당하는 코스가 없습니다.", HttpStatus.NOT_FOUND);
		}
		
		courseService.delete(course);
		
		return ResponseEntity.ok("삭제 시켰습니다.");
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
			courseService.delete(course);
		}
		
		return ResponseEntity.ok("삭제 시켰습니다.");
	}
	
	// 리뷰 삭제 : 여러 개
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/deleteReviews")
	public ResponseEntity<?> deleteReviews(@RequestBody Map<String, List<Integer>> map) {
		
		if (map.get("reviewSeqs").size() == 0 ) {
			return new ResponseEntity<>("삭제 시킬 후기가 없습니다", HttpStatus.BAD_REQUEST);
		}
		
		List<Integer> seqs = map.get("reviewSeqs");
		List<Integer> reviewSeqs = new ArrayList<>();
		
		for (Integer seq : seqs) {
			Review review = reviewService.getReview(seq);
			
			if (review == null) {
				return new ResponseEntity<>("존재하지 않는 후기를 삭제 시키려고 시도했습니다.", HttpStatus.NOT_FOUND);
			}
			
			reviewSeqs.add(seq);
		}
		
		for (Integer reviewSeq : reviewSeqs) {
			Review review = reviewService.getReview(reviewSeq);
			reviewService.delete(review);
		}
		
		return ResponseEntity.ok("삭제 시켰습니다.");
	}
	
}
