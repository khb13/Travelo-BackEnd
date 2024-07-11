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
	
	@Column(nullable = false, unique = true)
	private String nickname;
	
	@Column(nullable = false)
	private LocalDateTime registerDate;
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String delYN;
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String adminYN;
	
}
