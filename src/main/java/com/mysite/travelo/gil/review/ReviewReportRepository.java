package com.mysite.travelo.gil.review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Integer> {

//	사용자와 댓글 순차번호로 신고를 찾음
	Optional<ReviewReport> findByReviewAndAuthor(Review review, SiteUser author);
	
	List<ReviewReport> findByReview(Review review);
	
}
