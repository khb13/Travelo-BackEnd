package com.mysite.travelo.gil.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.travelo.hyo.place.Place;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CourseList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseListSeq;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="courseSeq")
	private Course course;
	
	// member는 비식별로 명시하지는 않았지만 course에서 가져옴
	
	@ManyToOne
	@JoinColumn(name = "placeSeq")
	private Place place;

}
