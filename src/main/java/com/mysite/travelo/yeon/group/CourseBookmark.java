package com.mysite.travelo.yeon.group;

import com.mysite.travelo.yeon.user.SiteUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CourseBookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseBookmarkSeq;
	
	@ManyToOne
	private SiteUser user;
	
	@ManyToOne
	private Course course;
	
}
