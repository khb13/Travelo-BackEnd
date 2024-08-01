package com.mysite.travelo.gil.review;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewReportService {

	private final ReviewReportRepository reportRepository;
	
	public void delete(Review review) {
		
		List<ReviewReport> reports = reportRepository.findByReview(review);
		
		for (ReviewReport report : reports) {
			this.reportRepository.delete(report);
		}
		
	}
	
}
