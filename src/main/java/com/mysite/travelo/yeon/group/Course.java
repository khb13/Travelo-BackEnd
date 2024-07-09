package com.mysite.travelo.yeon.group;

import java.time.LocalDateTime;

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
import java.util.List;

@Getter
@Setter
@Entity
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseSeq; // 코스 순차번호
	   
	@ManyToOne
	private SiteUser author; // userSeq(회원 순차번호) 참조
	   
	@Column(nullable = false, columnDefinition = "CHAR(2)")
	private String areaCode; // 코스의 지역 코드
	   
	@Column(nullable = false, columnDefinition = "VARCHAR(200)")
	private String title; // 코스명
	   
	@Column(nullable = true, columnDefinition = "VARCHAR(5000)")
	private String description; // 코스의 상세 설명
	   
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer viewCount; // 코스의 조회수
	   
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer likeCount; // 코스의 좋아요 수
	   
	@Column(nullable = false)
	private LocalDateTime createDate; // 코스의 작성일자
	   
	@Column(nullable = true)
	private LocalDateTime modifyDate; // 코스의 수정일자
	   
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String privateYn; // 코스의 비공개 여부
	   
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<CourseList> courseList; // 코스의 장소 목록
	   
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<Review> reviewList; // 코스의 후기 목록
	
}
