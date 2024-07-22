package com.mysite.travelo.gil.review;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController // JSON 형태로 반환할 것임을 명시
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"} , allowedHeaders = "*", allowCredentials = "true")
public class AdminReviewController {
	
	private final ReviewService reviewService;

//	블라인드 처리 대기 리뷰 목록(관리자 기능)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/blindReviewList")
	public Map<String, Object> list(Authentication auth,
									@RequestParam(value = "page", defaultValue = "0") int page,
									@RequestParam(value = "reportCount", defaultValue = "5") int reportCount) {
		
		Map<String, Object> response = new HashMap<>();
		
		Page<Map<String, Object>> tempBlindReviews = reviewService.getReviewsWithReportCount(page, reportCount);
		response.put("tempBlindReviews", tempBlindReviews);
	
		return response;
	}
	
//	리뷰를 블라인드 확정시키는 기능
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/blind/{reviewSeq}")
	public ResponseEntity<String> blind(Authentication auth,
											@PathVariable("reviewSeq") Integer reviewSeq) {
		
		reviewService.blindReview(reviewSeq);
		
		return new ResponseEntity<>("블라인드가 확정되었습니다.", HttpStatus.OK);
	}
	
//	리뷰의 신고 수 초기화시키는 기능
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/unblind/{reviewSeq}")
	public ResponseEntity<String> unblind(Authentication auth,
													@PathVariable("reviewSeq") Integer reviewSeq) {
		
		reviewService.removeBlindReview(reviewSeq);
		
		return new ResponseEntity<>("신고 수가 초기화되었습니다.", HttpStatus.OK);
	}
	
}
