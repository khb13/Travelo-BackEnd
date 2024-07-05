package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;

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
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userSeq;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, columnDefinition = "CHAR(11)")
	private String tel;
	
	@Column(nullable = false)
	private LocalDateTime registerDate;
	
	private LocalDateTime modifyDate;
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String delYn;
	
}