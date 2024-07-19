package com.mysite.travelo.yeon.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Integer> {

	OAuthToken findByUser(SiteUser user);
	
}
