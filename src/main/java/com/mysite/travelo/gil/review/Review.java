package com.mysite.travelo.gil.review;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.yeon.user.User;

import jakarta.persistence.Column;
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
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reviewSeq; // 후기 순차번호
	
	@ManyToOne
	private User user; // userSeq(회원 순차번호) 참조
	
	@ManyToOne
	@JsonBackReference
	private Course course; // courseSeq(코스 순차번호) 참조
	
	@Column(nullable = false, columnDefinition = "VARCHAR(5000)")
	private String content; // 후기 내용
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer recommendCount; // 후기 추천 수
	
	@Column(nullable = false)
	private LocalDateTime createDate; // 후기 작성일자
	
	@Column(nullable = true)
	private LocalDateTime modifyDate; // 후기 수정일자
}
