package com.mysite.travelo.gil.review;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewRecommendService {

	private final ReviewRecommendRepository recommendRepository;
	
	public void delete(Review review) {
		
		List<ReviewRecommend> recommendList = recommendRepository.findByReview(review);
		
		for (ReviewRecommend recommend : recommendList) {
			this.recommendRepository.delete(recommend);
		}
		
	}
	
}
