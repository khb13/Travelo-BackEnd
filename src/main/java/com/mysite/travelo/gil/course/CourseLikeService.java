package com.mysite.travelo.gil.course;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseLikeService {

	private final CourseLikeRepository courseLikeRepository;
	
	public void delete(Course course) {
		
		List<CourseLike> likes = courseLikeRepository.findByCourse(course);
		
		for (CourseLike like : likes) {
			this.courseLikeRepository.delete(like);
		}
		
	}
	
}
