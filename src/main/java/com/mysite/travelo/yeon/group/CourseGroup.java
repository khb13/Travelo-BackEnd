package com.mysite.travelo.yeon.group;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mysite.travelo.yeon.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CourseGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseGroupSeq;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(200)")
	private String title;
	
	@Column(nullable = false)
	private LocalDateTime createDate;
	
	private LocalDateTime modifyDate;
	
	@ManyToOne
	private SiteUser author;
	
	@OneToMany(mappedBy = "courseGroup", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<CourseGroupList> courseGroupList; // 코스의 장소 목록

}
