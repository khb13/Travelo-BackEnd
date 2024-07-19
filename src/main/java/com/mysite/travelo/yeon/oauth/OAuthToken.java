package com.mysite.travelo.yeon.oauth;

import com.mysite.travelo.yeon.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OAuthToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tokenSeq;
	
	@Column(nullable = false)
	private String accessToken;
	
	private String refreshToken;
	
	@ManyToOne
	private SiteUser user;
	
}
