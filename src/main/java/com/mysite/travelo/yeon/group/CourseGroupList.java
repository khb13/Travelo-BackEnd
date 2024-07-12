package com.mysite.travelo.yeon.group;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private CourseGroup courseGroup;

	@ManyToOne
	private Course course;
	
}
