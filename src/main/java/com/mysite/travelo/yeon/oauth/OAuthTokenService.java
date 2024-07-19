package com.mysite.travelo.yeon.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysite.travelo.yeon.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {

	@Autowired
	private OAuthTokenRepository oAuthTokenRepository;
	
	public void saveToken(Map<String, Object> map) {
		
		OAuthToken oAuthToken = new OAuthToken();
		oAuthToken.setAccessToken((String)map.get("accessToken"));
		oAuthToken.setRefreshToken((String)map.get("refreshToken"));
		oAuthToken.setUser((SiteUser)map.get("user"));
		
		oAuthTokenRepository.save(oAuthToken);
	}
	
	public OAuthToken getToken(SiteUser user) {
		
		OAuthToken oAuthToken = oAuthTokenRepository.findByUser(user);
		
		if (oAuthToken == null) {
			return null;
		}
		
		return oAuthToken;
	}
	
	public void modifyToken(Map<String, Object> map) {
		
		OAuthToken oAuthToken = oAuthTokenRepository.findByUser((SiteUser)map.get("user"));
		
		oAuthToken.setAccessToken((String)map.get("accessToken"));
		
		if (map.get("refreshToken") != null) {
			oAuthToken.setRefreshToken((String)map.get("refreshToken"));
		}
		
		oAuthTokenRepository.save(oAuthToken);
	}
	
	public void deleteToken(SiteUser user) {
		
		OAuthToken oAuthToken = oAuthTokenRepository.findByUser(user);
		
		oAuthTokenRepository.delete(oAuthToken);
	}
	
}
