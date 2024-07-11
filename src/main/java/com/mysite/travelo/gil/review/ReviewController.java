package com.mysite.travelo.gil.review;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.yeon.user.User;
import com.mysite.travelo.yeon.user.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

	private final CourseService courseService;
	private final ReviewService reviewService;
	private final UserRepository userRepository;

//	댓글 작성
	@PostMapping("create/{courseSeq}")
	public ResponseEntity<?> createReview(@PathVariable("courseSeq") Integer courseSeq,
								@RequestParam(value = "content") String content,
								@Valid ReviewForm reviewForm,
								BindingResult bindingResult) {
		
		Course course = this.courseService.getCourse(courseSeq);
		
//		임시 회원 정보
		Optional<User> ou = userRepository.findById(1);
		User user = null;
		
		if (ou.isPresent()) {
			user = ou.get();
		}
		
		if (bindingResult.hasErrors()) {
			
			// 자세한 오류 응답 생성
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            // 오류 세부 정보 및 HTTP 상태와 함께 ResponseEntity 반환
            return ResponseEntity.badRequest().body(errors);
		}
		
		this.reviewService.create(course, reviewForm.getContent(), user);
		
		return new ResponseEntity<>("댓글이 작성되었습니다.", HttpStatus.OK);
	}
	
//	댓글 수정
//	@PreAuthorize("isAuthenticated()") // 인증된 사용자에게만 메서드가 호출될수 있게 함
	@GetMapping("/modify/{reviewSeq}")
	public ResponseEntity<ReviewForm> modifyReview(@PathVariable("reviewSeq") Integer reviewSeq) {
		
		Review review = this.reviewService.getReview(reviewSeq);
		
		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setContent(review.getContent());
		
		return new ResponseEntity<>(reviewForm, HttpStatus.OK);
	}
	
//	댓글 수정
//	@PreAuthorize("isAuthenticated()") // 인증된 사용자에게만 메서드가 호출될수 있게 함
	@PostMapping("/modify/{reviewSeq}")
	public ResponseEntity<String> modifyReview(@Valid ReviewForm reviewForm,
								BindingResult bindingResult,
								@PathVariable("reviewSeq") Integer reviewSeq) {
		
		if (bindingResult.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder("Error: ");
            bindingResult.getFieldErrors().forEach(error -> errorMessage.append(error.getField()).append(" ").append(error.getDefaultMessage()).append(". "));
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
		
		Review review = this.reviewService.getReview(reviewSeq);
		
		// 기존 리뷰 내용과 수정한 리뷰 내용 비교 후 변경 된 내용이 있을 경우에만 저장
        if (!review.getContent().equals(reviewForm.getContent())) {
            this.reviewService.modify(review, reviewForm.getContent());
        }
        
		return new ResponseEntity<>("댓글이 수정되었습니다.", HttpStatus.OK);
	}
	
//	댓글 삭제
//	@PreAuthorize("isAuthenticated()") // 인증된 사용자에게만 메서드가 호출될수 있게 함
	@GetMapping("/delete/{reviewSeq}")
	public ResponseEntity<String> delete(@PathVariable("reviewSeq") Integer reviewSeq) {
		
		Review review = reviewService.getReview(reviewSeq);
		
		reviewService.delete(review);
		
		return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
	}
	
//	댓글 추천 증가
	@PostMapping("/{reviewSeq}/recommend")
	public ResponseEntity<String> recommend(@PathVariable("reviewSeq") Integer reviewSeq) {
		
		reviewService.increaseRecommendCount(reviewSeq);
		
		return new ResponseEntity<>("추천이 1 증가했습니다.", HttpStatus.OK);
	}
	
//	댓글 추천 감소
	@PostMapping("/{reviewSeq}/removeRecommend")
	public ResponseEntity<String> removeRecommend(@PathVariable("reviewSeq") Integer reviewSeq) {

		reviewService.decreaseRecommendCount(reviewSeq);
		
		return new ResponseEntity<>("추천이 1 감소했습니다.", HttpStatus.OK);
	}
	
	
}
