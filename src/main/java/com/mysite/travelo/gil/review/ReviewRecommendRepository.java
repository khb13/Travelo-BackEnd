package com.mysite.travelo.gil.review;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommend, Integer> {

//	사용자와 댓글 순차번호로 추천을 찾음
	Optional<ReviewRecommend> findByReviewAndAuthor(Review review, SiteUser author);
}
