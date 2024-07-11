package com.mysite.travelo.gil.course;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.User;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Integer> {

//	회원정보와 코스 정보의 존재 여부 확인
	boolean existsByUserAndCourse(User user, Course course);

//	회원 정보와 코스북마크의 존재 여부 확인
	Optional<CourseBookmark> findByUserAndCourseBookmarkSeq(User user, Integer courseBookmarkSeq);
	
}
