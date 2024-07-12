package com.mysite.travelo.gil.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Integer> {

//	회원정보와 코스 정보의 존재 여부 확인
	boolean existsByUserAndCourse(SiteUser user, Course course);

//	회원 정보와 코스북마크의 존재 여부 확인
	Optional<CourseBookmark> findByUserAndCourseBookmarkSeq(SiteUser user, Integer courseBookmarkSeq);
	
	List<CourseBookmark> findByUser(SiteUser user);
	List<CourseBookmark> findByUserAndCourse_AreaCode(SiteUser user, String areaCode);
}
