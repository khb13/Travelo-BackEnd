package com.mysite.travelo.yeon.group;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseGroupRepository extends JpaRepository<CourseGroup, Integer> {

	List<CourseGroup> findByAuthorUsername(String username);
	Page<CourseGroup> findByAuthorUsername(String username, Pageable pageable);
	
}
