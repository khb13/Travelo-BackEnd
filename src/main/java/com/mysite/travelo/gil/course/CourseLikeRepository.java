package com.mysite.travelo.gil.course;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Integer> {
	
//	사용자와 코스 순차번호로 좋아요를 찾음
	Optional<CourseLike> findByCourseAndAuthor(Course course, SiteUser author);
}
