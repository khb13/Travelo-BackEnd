package com.mysite.travelo.gil.course;

import com.mysite.travelo.yeon.user.SiteUser;

import jakarta.persistence.Column;
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
public class CourseLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseLikeSeq; // 코스 좋아요 순차번호
	
	@ManyToOne
	@JoinColumn(name="userSeq")
	private SiteUser author; // userSeq(회원 순차번호) 참조
	
	@ManyToOne
	@JoinColumn(name="courseSeq")
	private Course course; // courseSeq(코스 순차번호) 참조
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String likeYn; // 코스의 좋아요 여부
}
