package com.mysite.travelo.yeon.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.travelo.gil.course.Course;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CourseGroupList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseGroupListSeq;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name="courseGroupSeq")
	private CourseGroup courseGroup;

	@ManyToOne
	@JoinColumn(name="courseSeq")
	private Course course;
	
}
