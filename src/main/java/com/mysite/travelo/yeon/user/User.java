package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer UserSeq;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false, unique = true)
	private String nickname;


	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime registerDate;

	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String delYn;

	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String adminYn;
	
}
