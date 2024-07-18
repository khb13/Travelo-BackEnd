package com.mysite.travelo.gil.review;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.yeon.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	
//	특정 코스의 댓글 목록 조회(정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	public Page<Review> getReviews(int page, Integer courseSeq, String sortBy) {
		
		Pageable pageable;
		
//		정렬
        if ("popularity".equals(sortBy)) {
            pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "recommendCount").and(Sort.by(Sort.Direction.DESC, "createDate"))); // 추천 많은순, 최신순
        } else if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "recommendCount").and(Sort.by(Sort.Direction.DESC, "createDate"))); // 추천 많은순, 최신순(디폴트)
        }
		
		return reviewRepository.findByCourseCourseSeqAndBlindYn(pageable, courseSeq, "N");
	}
	
//	특정 코스의 댓글 갯수 조회
    public int getReviewsCountByCourse(Integer courseSeq) {
    	
        return reviewRepository.countByCourseCourseSeqAndBlindYn(courseSeq, "N");
    }
	
//	특정 유저의 공개 댓글 목록 조회(정렬 디폴트값: 최신순 / 옵션값: 오래된순)
	public Page<Map<String, Object>> getMyReviews(int page, Integer userSeq, String sortBy) {
		
		Pageable pageable;
		
//		정렬
		if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
		} else if ("popularity".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "recommendCount")); // 추천순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
		}
		
		Page<Review> reviews = reviewRepository.findByUserUserSeqAndBlindYn(pageable, userSeq, "N");
		
//		해당 댓글이 참조하는 코스의 순차번호와 코스명을 같이 반환
		return reviews.map(review -> {
            Map<String, Object> map = new HashMap<>();
            map.put("review", review);
            map.put("courseSeq", review.getCourse().getCourseSeq());
            map.put("courseTitle", review.getCourse().getTitle());
            return map;
        });
	}
	
//	특정 유저의 비공개 댓글 갯수 조회
	public int getBlindReviewsCountByUser(Integer userSeq) {
		
		return reviewRepository.countByUserUserSeqAndBlindYn(userSeq, "Y");
	}
	
//	신고 수 5회 이상인 공개 댓글 전체보기(정렬 디폴트값: 오래된순)
	public Page<Map<String, Object>> getReviewsWithReportCount(int page, int reportCount) {
		
		Pageable pageable = PageRequest.of(page, 15, Sort.Direction.ASC, "createDate"); // 오래된순(디폴트)
		
		Page<Review> tempBlindReviews = reviewRepository.findByReportCountGreaterThanEqualAndBlindYn(pageable, reportCount, "N");
		
//		해당 댓글이 참조하는 코스의 순차번호와 코스명을 같이 반환
		return tempBlindReviews.map(review -> {
			Map<String, Object> map = new HashMap<>();
			map.put("review", review);
			map.put("courseSeq", review.getCourse().getCourseSeq());
			map.put("courseTitle", review.getCourse().getTitle());
			return map;
		});
	}
	
//	댓글 작성
	public void create(Course course, String content, SiteUser user) {
		Review review = new Review();
		review.setContent(content);
		review.setUser(user); // 임시 회원 정보
		review.setCreateDate(LocalDateTime.now());
	    review.setRecommendCount(0); // 기본값 설정
	    review.setReportCount(0); // 기본값 설정
	    review.setBlindYn("N"); // 기본값 설정
		review.setCourse(course);
		this.reviewRepository.save(review);
	}
	
//	댓글 조회
	public Review getReview(Integer reviewSeq) {
		Optional<Review> or = this.reviewRepository.findById(reviewSeq);
		if(or.isPresent()) {
			return or.get();
		} else {
			throw new DataNotFoundException("댓글을 찾을 수 없습니다.");
		}
	}
	
//	댓글 수정
	public void modify(Review review, String content) {
		review.setContent(content);
		review.setModifyDate(LocalDateTime.now());
		this.reviewRepository.save(review);
	}
	
//	댓글 삭제
	public void delete(Review review) {
		this.reviewRepository.delete(review);
	}
	
//	댓글 추천 수 증가
	public void increaseRecommendCount(Integer reviewSeq) {
		
		Review review = getReview(reviewSeq);
        review.setRecommendCount(review.getRecommendCount() + 1);
        reviewRepository.save(review);
	}
	
//	댓글 추천 수 감소
	public void decreaseRecommendCount(Integer reviewSeq) {
		
		Review review = getReview(reviewSeq);
        review.setRecommendCount(review.getRecommendCount() - 1);
        reviewRepository.save(review);
	}
	
//	댓글 신고 수 증가
	public void increaseReportCount(Integer reviewSeq) {
		
		Review review = getReview(reviewSeq);
		review.setReportCount(review.getReportCount() + 1);
		reviewRepository.save(review);
		
	}
	
//	댓글 블라인드 처리
	public void blindReview(Integer reviewSeq) {
		
		Review review = getReview(reviewSeq);
		review.setBlindYn("Y");
		reviewRepository.save(review);
	}
	
//	댓글 신고 수 0 처리
	public void removeBlindReview(Integer reviewSeq) {
		
		Review review = getReview(reviewSeq);
		review.setReportCount(0);
		reviewRepository.save(review);
	}

//	블라인드 처리된 댓글 불러오기
	public List<Review> getBlindReview(SiteUser loginUser) {
		
		List<Review> blindReviews = reviewRepository.findByBlindYnAndUserUserSeq("Y", loginUser.getUserSeq());
		
		return blindReviews;

	}
	
	public List<Review> blindReviewAll() {
		
		return reviewRepository.findByBlindYn("Y");
	}
	
	public List<Review> getAllReviewCount() {
		
		return reviewRepository.findAll(); 
	}

	public List<Review> getReportReviewCount() {
		
		return reviewRepository.findByReportCountGreaterThanEqual(5);
	}
	
	public Page<Review> getAllReview(int page, String sortBy) {
		
		Pageable pageable = PageRequest.of(page, 15);
		
		if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
		} else if ("oldest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된 순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
		}
		
		return reviewRepository.findAll(pageable);
	}
	
	public Page<Review> getBlindReviews(int page, String blindYn, String sortBy) {
		
		Pageable pageable = PageRequest.of(page, 15);
		
		if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
		} else if ("oldest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된 순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
		}
		
		return reviewRepository.findByBlindYn(blindYn, pageable);
	}
	
	public Page<Review> getAllReviewByUser(Integer userSeq, int page, String sortBy) {
		
		Pageable pageable = PageRequest.of(page, 15);
		
		if ("latest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
		} else if ("oldest".equals(sortBy)) {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된 순
		} else {
			pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
		}
		
		return reviewRepository.findByUserUserSeq(userSeq, pageable);
		
	}
	
}
