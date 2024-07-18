package com.mysite.travelo.yeon.user;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"})
public class KakaoController {

	private final UserService userService;
	private final JWTUtil jwtUtil;
	
	@Value("${KAKAO_KEY}")
	private String kakao;
	
	@GetMapping("/travelo/kakaoCallback")
	public ResponseEntity<?> kakaoCallback(@RequestParam String code) {

		// RestTemplate 생성
	    RestTemplate restTemplate = new RestTemplate();

	    // 헤더 설정
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

	    // 바디 설정
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("grant_type", "authorization_code");
	    params.add("client_id", kakao); // Kakao에서 발급한 REST API Key 입력
	    params.add("redirect_uri", "http://localhost:8080/travelo/kakaoCallback"); // Kakao 개발자 센터에서 설정한 리다이렉트 URI
	    params.add("code", code);

	    // 헤더 + 바디 결합
	    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

	    // Kakao OAuth 토큰 요청
	    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	            "https://kauth.kakao.com/oauth/token",
	            HttpMethod.POST,
	            requestEntity,
	            new ParameterizedTypeReference<Map<String, Object>>() {});

	    // 응답에서 access_token
	    String accessToken = (String) response.getBody().get("access_token");

	    // 사용자 정보 요청
	    ResponseEntity<String> response2 = getUserInfoFromKakao(accessToken);

	    // 사용자 정보에서 이메일 추출
	    String username = extractEmail(response2.getBody());
//	    String tel = extractPhoneNumber(response2.getBody());

	    SiteUser oldUser = userService.getUser(username);
	    
	    if (oldUser == null) {
	        userService.joinKakao(username);
	        oldUser = userService.getUser(username);
	    }
	    
	    if (oldUser.getDelYn().equals("Y")) {
	    	return new ResponseEntity<>("탈퇴한 회원입니다", HttpStatus.BAD_REQUEST);
	    }

	    accessToken = jwtUtil.createJwt(oldUser.getUsername(), oldUser.getRole().toString(), 1000 * 60 * 60L);
        String refreshToken = jwtUtil.generateRefreshToken(oldUser.getUsername(), oldUser.getRole().toString(), 1000 * 60 * 60 * 24 * 7);
	    
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);

        return ResponseEntity.ok().body(authResponse);
	}
	
	// 액세스 토큰을 사용하여 Kakao 사용자 정보 요청하는 메서드
	private ResponseEntity<String> getUserInfoFromKakao(String accessToken) {
	    RestTemplate restTemplate = new RestTemplate();

	    // 헤더
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", "Bearer " + accessToken);
	    headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

	    HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(headers);
	    ResponseEntity<String> response = restTemplate.exchange(
	            "https://kapi.kakao.com/v2/user/me",
	            HttpMethod.POST,
	            kakaoInfo,
	            String.class);

	    return response;
	}
	
	// JSON에서 이메일 추출하는 메서드
    private String extractEmail(String json) {
        int startIndex = json.indexOf("\"email\":\"") + "\"email\":\"".length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }
    
    // 로그아웃
    @GetMapping("/user/kakaoLogout")
    public ResponseEntity<String> kakaoLogout() {
    	
    	return ResponseEntity.ok("카카오 로그아웃 성공");
    }
}
