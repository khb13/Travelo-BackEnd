package com.mysite.travelo.hyo.place;

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
public class PlaceLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer placeLikeSeq; // 장소 좋아요 순차번호
	
	@ManyToOne
	@JoinColumn(name="userSeq")
	private SiteUser author; // userSeq(회원 순차번호) 참조
	
	@ManyToOne
	@JoinColumn(name="placeSeq")
	private Place place; // placeSeq(장소 순차번호) 참조
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String likeYn; // 장소의 좋아요 여부
}
