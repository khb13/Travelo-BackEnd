package com.mysite.travelo.yeon.group;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.travelo.yeon.user.SiteUser;


public interface CourseRepository extends JpaRepository<Course, Integer> {

	boolean existsById(Integer courseSeq);
	
	List<Course> findAllByPrivateYnOrderByLikeCountDescViewCountDesc(String privateYn, Pageable pageable);
	
	List<Course> findByAuthor(SiteUser author);
	List<Course> findByAuthorAndAreaCode(SiteUser author, String areaCode);
	
}
