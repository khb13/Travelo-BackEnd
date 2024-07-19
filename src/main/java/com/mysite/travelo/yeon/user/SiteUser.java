package com.mysite.travelo.yeon.user;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userSeq;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(columnDefinition = "CHAR(11)")
	private String tel;
	
	@Column(nullable = false)
	private LocalDateTime registerDate;
	
	private LocalDateTime modifyDate;
	
	@Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
	private String delYn;

	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	private String oauthType;
	
	private String oauthId;
	
}