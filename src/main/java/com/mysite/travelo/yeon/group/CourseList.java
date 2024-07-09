package com.mysite.travelo.yeon.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.travelo.hyo.place.Place;

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
public class CourseList {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseListSeq;
   
    @ManyToOne
    @JsonBackReference
    private Course course;
   
    // user는 비식별로 명시하지는 않았지만 course에서 가져옴
   
    @ManyToOne
    private Place place;

}
