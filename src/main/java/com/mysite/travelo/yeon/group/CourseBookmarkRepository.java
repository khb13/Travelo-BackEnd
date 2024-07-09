package com.mysite.travelo.yeon.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Integer> {

	List<CourseBookmark> findByUser(SiteUser user);
	List<CourseBookmark> findByUserAndCourse_AreaCode(SiteUser user, String areaCode);
	 
}
