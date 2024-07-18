package com.mysite.travelo.yeon.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.mysite.travelo.yeon.user.AuthResponse;
import com.mysite.travelo.yeon.user.JWTUtil;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


@Controller
@RequiredArgsConstructor
public class NaverController {

	private final UserService userService;
	private final JWTUtil jwtUtil;
	
	@Value("${NAVER_CLIENT_ID}")
	private String clientId;
	
	@Value("${NAVER_SECRET_KEY}")
	private String scecretKey;
	
	@GetMapping("/travelo/naverCallback")
	public ResponseEntity<?> naverCallback(@RequestParam("code") String code) {
		
		// RestTemplate 생성
	    RestTemplate restTemplate = new RestTemplate();

	    // 헤더 설정
	    HttpHeaders headers1 = new HttpHeaders();
	    headers1.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", scecretKey);
		params.add("code", code);
		params.add("redirect_uri", "http://localhost:8080/travelo/naverCallback");
		params.add("state", "STATE_STRING");
		
		 // 헤더 + 바디 결합
	    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers1);
		
		// OAuth 토큰 요청
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        		"https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {});

	    // 액세스 토큰
	    String accessToken = (String) response.getBody().get("access_token");

	    System.out.println(accessToken);
	    
	    // 사용자 정보 요청
	    HttpHeaders headers2 = new HttpHeaders();
	    headers2.add("Authorization", "Bearer "+ accessToken);
	    headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
	    HttpEntity<MultiValueMap<String, String>> naverInfo
	        = new HttpEntity<>(headers2);

	    ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
	            "https://openapi.naver.com/v1/nid/me",
	            HttpMethod.POST,
	            naverInfo,
	            new ParameterizedTypeReference<Map<String, Object>>() {});

	    
	    Map<String, Object> responseBody = userInfoResponse.getBody();
	    Map<String, Object> response2 = (Map<String, Object>) responseBody.get("response");
	    String email = (String) response2.get("email");
	    String tel = (String) response2.get("mobile_e164");
	    tel = "0" + extractPhoneNumber(tel);
	    
        // 사용자 정보로 회원 조회
	    SiteUser oldUser = userService.getUser(email);

	    // 회원이 존재하지 않으면 회원 가입 처리
	    if (oldUser == null) {
	        userService.joinNaver(email, tel);
	        oldUser = userService.getUser(email);
	    }

	    // 탈퇴한 회원인 경우 처리
	    if ("Y".equals(oldUser.getDelYn())) {
	        return new ResponseEntity<>("탈퇴한 회원입니다", HttpStatus.BAD_REQUEST);
	    }

	    // JWT 토큰 생성 및 반환
	    String jwtToken = jwtUtil.createJwt(oldUser.getUsername(), oldUser.getRole().toString(), 1000 * 60 * 60L);
	    String refreshToken = jwtUtil.generateRefreshToken(oldUser.getUsername(), oldUser.getRole().toString(), 1000 * 60 * 60 * 24 * 7);

	    AuthResponse authResponse = new AuthResponse(jwtToken, refreshToken);

	    return ResponseEntity.ok().body(authResponse);
	}
	
	private String extractPhoneNumber(String json) {
	    // 전화번호를 포함한 부분 찾기
	    int startIndex = json.indexOf("+82") + "+82".length();
	    String phoneNumberWithPrefix = json.substring(startIndex);
	    
	    // 숫자만 남기기
	    String phoneNumber = phoneNumberWithPrefix.replaceAll("[^0-9]", "");
	    
	    return phoneNumber;
	}
	
}
