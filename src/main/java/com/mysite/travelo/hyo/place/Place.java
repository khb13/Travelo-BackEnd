package com.mysite.travelo.hyo.place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer pSeq; // 장소 순차번호
	
	@Column(nullable = false, length = 2)
	private String pAreaCode; // 지역 코드
	
	@Column(nullable = false, length = 2)
	private String type; // 콘텐츠 타입(유형)
	
	@Column(nullable = false, length = 200)
	private String pTitle; //제목(장소명)
	
	private String tel; // 전화번호
	
	@Column(nullable = false, length = 5)
	private String zipCode; // 우편번호

	private String district; // 시/도 구분
	
	@Column(nullable = false, length = 255)
	private String address; // 주소
	
	@Column(length = 100)
	private String addressDetail; // 상세주소
	
	@Column(nullable = false, columnDefinition = "double(10, 6)")
	private double latitude; // 위도 좌표 y
	
	@Column(nullable = false, columnDefinition = "double(10, 6)")
	private double longitude; // 경도 좌표 x
	
	@Column(length = 255)
	private String imageFile1; // 대표 이미지(원본)
	
	@Column(length = 255)
	private String imageFile2; // 대표 이미지(썸네일)
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer pViewCount; // 조회수
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer pLikeCount; // 좋아요 수
}
