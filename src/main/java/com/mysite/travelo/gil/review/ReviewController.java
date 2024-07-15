package com.mysite.travelo.gil.review;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user/review")
public class ReviewController {

	private final UserService userService;
	private final CourseService courseService;
	private final ReviewService reviewService;

//	댓글 작성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("create/{courseSeq}")
	public ResponseEntity<?> createReview(Authentication auth,
											@PathVariable("courseSeq") Integer courseSeq,
											@Valid @RequestBody ReviewForm reviewForm,
											BindingResult bindingResult) {
		
		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
		
		Course course = this.courseService.getCourse(courseSeq);
		
		if (bindingResult.hasErrors()) {
			
			// 자세한 오류 응답 생성
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put("error", error.getDefaultMessage()));
            // 오류 세부 정보 및 HTTP 상태와 함께 ResponseEntity 반환
            return ResponseEntity.badRequest().body(errors);
		}
		
		this.reviewService.create(course, reviewForm.getContent(), loginUser);
		
		return new ResponseEntity<>("댓글이 작성되었습니다.", HttpStatus.OK);
	}
	
//	댓글 수정
	@PreAuthorize("isAuthenticated()") // 인증된 사용자에게만 메서드가 호출될수 있게 함
	@PostMapping("/modify/{reviewSeq}")
	public ResponseEntity<String> modifyReview(@Valid @RequestBody ReviewForm reviewForm,
												BindingResult bindingResult,
												@PathVariable("reviewSeq") Integer reviewSeq) {
		
		if (bindingResult.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder("Error: ");
            bindingResult.getFieldErrors().forEach(error -> errorMessage.append(error.getField()).append(" ").append(error.getDefaultMessage()));
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
		
		Review review = this.reviewService.getReview(reviewSeq);
		
		// 기존 리뷰 내용과 수정한 리뷰 내용 비교 후 변경 된 내용이 있을 경우에만 저장
        if (review.getContent().equals(reviewForm.getContent())) {
        	return new ResponseEntity<>("기존 내용과 같아 수정이 되지 않았습니다", HttpStatus.BAD_REQUEST);
        }
        
        this.reviewService.modify(review, reviewForm.getContent());
        
		return new ResponseEntity<>("댓글이 수정되었습니다.", HttpStatus.OK);
	}
	
//	댓글 삭제
	@PreAuthorize("isAuthenticated()") // 인증된 사용자에게만 메서드가 호출될수 있게 함
	@PostMapping("/delete/{reviewSeq}")
	public ResponseEntity<String> delete(@PathVariable("reviewSeq") Integer reviewSeq) {
		
		Review review = reviewService.getReview(reviewSeq);
		
		reviewService.delete(review);
		
		return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
	}
	
//	댓글 추천 증가
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/recommend/{reviewSeq}")
	public ResponseEntity<String> recommend(@PathVariable("reviewSeq") Integer reviewSeq) {
		
		reviewService.increaseRecommendCount(reviewSeq);
		
		return new ResponseEntity<>("추천이 1 증가했습니다.", HttpStatus.OK);
	}
	
//	댓글 추천 감소
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/removeRecommend/{reviewSeq}")
	public ResponseEntity<String> removeRecommend(@PathVariable("reviewSeq") Integer reviewSeq) {

		reviewService.decreaseRecommendCount(reviewSeq);
		
		return new ResponseEntity<>("추천이 1 감소했습니다.", HttpStatus.OK);
	}
	
	
}
