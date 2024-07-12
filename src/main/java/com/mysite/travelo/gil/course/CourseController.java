package com.mysite.travelo.gil.course;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.gil.review.Review;
import com.mysite.travelo.gil.review.ReviewForm;
import com.mysite.travelo.gil.review.ReviewService;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController // JSON 형태로 반환할 것임을 명시
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
	
	private final UserService userService;
	private final CourseService courseService;
	private final ReviewService reviewService;
	private final CourseBookmarkService courseBookmarkService;

//	코스 전체보기(정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public Map<String, Object> list(@RequestParam(value = "page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "popularity") String sortBy,
						@RequestParam(value = "areaCode", defaultValue = "") String areaCode,
						@RequestParam(value = "type", defaultValue = "") String type,
						Authentication auth) {
		
		Map<String, Object> response = new HashMap<>();
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		response.put("loginUser", loginUser);
		
		Page<Course> paging = courseService.getCourses(page, "N", sortBy, areaCode, type);
        response.put("paging", paging);
		
		return response;
	}
	
//	코스 상세보기
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail/{courseSeq}")
	public Map<String, Object> detail(
						@PathVariable("courseSeq") Integer courseSeq,
						@RequestParam(value="page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "popularity") String sortBy,
						ReviewForm reviewForm, Authentication auth) {
		
		Map<String, Object> response = new HashMap<>();
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		response.put("loginUser", loginUser);
		
		Course course = courseService.getCourse(courseSeq);
		response.put("course", course);
		
//		코스 조회수
		courseService.increaseViewCount(courseSeq);
		
//		정렬 기준에 따라 댓글 목록 조회
		Page<Review> paging = reviewService.getReviews(page, courseSeq, sortBy);
		response.put("paging", paging);
		
//		코스의 리뷰 개수 조회
        int reviewCount = reviewService.getReviewsCountByCourse(courseSeq);
        response.put("reviewCount", reviewCount);
		
		return response;
	}
	
//		코스 좋아요 수 증가
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/like/{courseSeq}")
	public ResponseEntity<String> like(@PathVariable("courseSeq") Integer courseSeq) {
		
	    courseService.increaseLikeCount(courseSeq);
	    
	    return new ResponseEntity<>("좋아요가 1 증가했습니다.", HttpStatus.OK);
	}
	
//		코스 좋아요 수 감소
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/removeLike/{courseSeq}")
	public ResponseEntity<String> removeLike(@PathVariable("courseSeq") Integer courseSeq) {
		
	    courseService.decreaseLikeCount(courseSeq);
	    
	    return new ResponseEntity<>("좋아요가 1 감소했습니다.", HttpStatus.OK);
	}
	
//	코스 북마크에 코스 추가
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/bookmark/{courseSeq}")
	public ResponseEntity<String> bookmark(Authentication auth,
							@PathVariable("courseSeq") Integer courseSeq) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		
		courseBookmarkService.addBookmark(loginUser.getUserSeq(), courseSeq);
		
		return new ResponseEntity<>("북마크가 추가되었습니다.", HttpStatus.OK);
	}
	
//	코스 북마크에 북마크된 코스 삭제
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/removeBookmark/{courseBookmarkSeq}")
	public ResponseEntity<String> removeBookmark(Authentication auth,
							@PathVariable("courseBookmarkSeq") Integer courseBookmarkSeq) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		
		courseBookmarkService.removeBookmark(loginUser.getUserSeq(), courseBookmarkSeq);
		
		return new ResponseEntity<>("북마크가 삭제되었습니다.", HttpStatus.OK);
	}
	
//	마이페이지에서의 유저의 리뷰 목록 조회(정렬 디폴트값: 최신순 / 옵션값: 추천순)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myReviews")
	public Map<String, Object> myReviews(Authentication auth,
										@RequestParam(value = "page", defaultValue = "0") int page,
										@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Map<String, Object> response = new HashMap<>();
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		Page<Map<String, Object>> paging = reviewService.getMyReviews(page, loginUser.getUserSeq(), sortBy);

		response.put("loginUser", loginUser);
		response.put("paging", paging);
		
		return response;
	}
	
}
