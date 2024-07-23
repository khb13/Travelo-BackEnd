package com.mysite.travelo.gil.review;

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
public class ReviewReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reviewReportSeq; // 댓글 신고 순차번호
	
	@ManyToOne
	@JoinColumn(name="userSeq")
	private SiteUser author; // userSeq(회원 순차번호) 참조
	
	@ManyToOne
	@JoinColumn(name="reviewSeq")
	private Review review; // reviewSeq(댓글 순차번호) 참조
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String reportYn; // 댓글의 신고 여부
}
