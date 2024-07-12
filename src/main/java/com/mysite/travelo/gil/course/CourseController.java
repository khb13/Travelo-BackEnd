package com.mysite.travelo.gil.course;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.gil.review.Review;
import com.mysite.travelo.gil.review.ReviewForm;
import com.mysite.travelo.gil.review.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController // JSON 형태로 반환할 것임을 명시
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
	
	private final CourseService courseService;
	private final ReviewService reviewService;
	private final CourseBookmarkService courseBookmarkService;

//	코스 전체보기(정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	@GetMapping("/list")
	public Map<String, Object> list(@RequestParam(value = "page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "popularity") String sortBy,
						@RequestParam(value = "areaCode", defaultValue = "") String areaCode,
						@RequestParam(value = "type", defaultValue = "") String type) {
		
		Map<String, Object> response = new HashMap<>();
		
		Page<Course> paging = courseService.getCourses(page, "N", sortBy, areaCode, type);
        response.put("paging", paging);
		
		return response;
	}
	
//	코스 상세보기
	@GetMapping("/detail/{courseSeq}")
	public Map<String, Object> detail(
						@PathVariable("courseSeq") Integer courseSeq,
						@RequestParam(value="page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "popularity") String sortBy,
						ReviewForm reviewForm) {
		
		Map<String, Object> response = new HashMap<>();
		
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
	@PostMapping("/{courseSeq}/like")
	public ResponseEntity<String> like(@PathVariable("courseSeq") Integer courseSeq) {
		
	    courseService.increaseLikeCount(courseSeq);
	    
	    return new ResponseEntity<>("좋아요가 1 증가했습니다.", HttpStatus.OK);
	}
	
//		코스 좋아요 수 감소
	@PostMapping("/{courseSeq}/removeLike")
	public ResponseEntity<String> removeLike(@PathVariable("courseSeq") Integer courseSeq) {
		
	    courseService.decreaseLikeCount(courseSeq);
	    
	    return new ResponseEntity<>("좋아요가 1 감소했습니다.", HttpStatus.OK);
	}
	
//	코스 북마크에 코스 추가
	@PostMapping("/{userSeq}/bookmark/{courseSeq}")
	public ResponseEntity<String> bookmark(@PathVariable("userSeq") Integer userSeq,
							@PathVariable("courseSeq") Integer courseSeq) {
		
		courseBookmarkService.addBookmark(userSeq, courseSeq);
		
		return new ResponseEntity<>("북마크가 추가되었습니다.", HttpStatus.OK);
	}
	
//	코스 북마크에 북마크된 코스 삭제
	@PostMapping("/{userSeq}/removeBookmark/{courseBookmarkSeq}")
	public ResponseEntity<String> removeBookmark(@PathVariable("userSeq") Integer userSeq,
							@PathVariable("courseBookmarkSeq") Integer courseBookmarkSeq) {
		
		courseBookmarkService.removeBookmark(userSeq, courseBookmarkSeq);
		
		return new ResponseEntity<>("북마크가 삭제되었습니다.", HttpStatus.OK);
	}
	
//	연경이가 만든 마이페이지에 넣을 특정 유저의 리뷰 목록 조회(정렬 디폴트값: 최신순 / 옵션값: 추천순)
	@GetMapping("/{userSeq}/myReviews")
	public Map<String, Object> myReviews(@PathVariable("userSeq") Integer userSeq,
										@RequestParam(value = "page", defaultValue = "0") int page,
										@RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
		
		Map<String, Object> response = new HashMap<>();
		
		Page<Map<String, Object>> paging = reviewService.getMyReviews(page, userSeq, sortBy);

		response.put("paging", paging);
		
		return response;
	}
	
}
