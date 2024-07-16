package com.mysite.travelo.gil.course;

import com.mysite.travelo.yeon.user.SiteUser;

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
public class CourseBookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseBookmarkSeq; // 코스 북마크 순차번호
	
	@ManyToOne
	@JoinColumn(name="userSeq")
	private SiteUser user; // userSeq(유저 순차번호) 참조
	
	@ManyToOne
//	@JsonBackReference
	@JoinColumn(name="courseSeq")
	private Course course; // courseSeq(코스 순차번호) 참조
}
