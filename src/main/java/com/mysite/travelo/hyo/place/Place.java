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
	private Integer pSeq;
	
	@Column(nullable = false, length = 2)
	private String pAreaCode;
	
	@Column(nullable = false, length = 2)
	private String type;
	
	@Column(nullable = false, length = 200)
	private String pTitle;
	
	private String tel;
	
	@Column(nullable = false, length = 5)
	private String zipCode;
	
	@Column(nullable = false, length = 255)
	private String address;
	
	@Column(length = 100)
	private String addressDetail;
	
	@Column(nullable = false, columnDefinition = "double(10, 6)")
	private double latitude;
	
	@Column(nullable = false, columnDefinition = "double(10, 6)")
	private double longitude;
	
	@Column(length = 255)
	private String imageFile1;
	
	@Column(length = 255)
	private String imageFile2;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer pViewCount;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private Integer pLikeCount;
	
}
