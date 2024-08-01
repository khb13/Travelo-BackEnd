package com.mysite.travelo.yeon.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.gil.course.Course;

public interface CourseGroupListRepository extends JpaRepository<CourseGroupList, Integer> {

	List<CourseGroupList> findByCourse(Course course);
	
}
