package com.mysite.travelo.yeon.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@GetMapping("/yeon/login")
	public String getMethodName() {
		return "/yeon/login";
	}
	
	@GetMapping("/kakao-callback")
	public String kakaoCallback(@RequestParam String code, HttpSession session) {

		// RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "4dc83b1961037fca002fa6eb9eee2bac"); // Kakao에서 발급한 REST API Key 입력
        params.add("redirect_uri", "http://localhost:8080/kakao-callback"); // Kakao 개발자 센터에서 설정한 리다이렉트 URI
        params.add("code", code);

        // 헤더 + 바디 결합
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // Kakao OAuth 토큰 요청
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {});

        // 응답에서 access_token 추출
        String accessToken = (String) response.getBody().get("access_token");

        // 사용자 정보 요청
        RestTemplate restTemplate2 = new RestTemplate();

        // 헤더
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + accessToken);
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(headers2);
        ResponseEntity<String> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoInfo,
                String.class);

        // 사용자 정보에서 이메일 추출
        String email = extractEmail(response2.getBody());

        Member oldMember = memberService.getMember(email);
        if (oldMember == null) {
        	session.setAttribute("email", email);
            return "redirect:/yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환
        }

        // 세션에 사용자 정보 저장
        session.setAttribute("member", oldMember);

        // 응답 반환
        return "redirect:/";
    }

    // JSON에서 이메일 추출하는 메서드
    private String extractEmail(String json) {
        int startIndex = json.indexOf("\"email\":\"") + "\"email\":\"".length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }

    @GetMapping("/yeon/signup")
    public String signup(MemberCreateForm memberCreateForm, Model model, HttpSession session) {
    	String email = (String)session.getAttribute("email");
        model.addAttribute("email", email);
        memberCreateForm.setUsername(email);
        return "yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환
    }

    @PostMapping("/yeon/signup")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환
        }

        Member member = memberService.getMember2(memberCreateForm.getNickname());

        if (member != null) {
            bindingResult.rejectValue("nickname", "이미 존재하는 닉네임입니다.");
            return "yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환
        }

        Map<String, String> map = new HashMap<>();
        map.put("username", memberCreateForm.getUsername());
        map.put("nickname", memberCreateForm.getNickname());

        try {
            memberService.create(map);

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");  // 이메일 중복 값일 수도 있으니까
            return "yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환

        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());  // 이메일 중복 값일 수도 있으니까
            return "yeon/signup"; // 회원 가입 폼 뷰 파일 경로 반환
        }

        Member oldMember = memberService.getMember(memberCreateForm.getUsername());
        session.setAttribute("member", oldMember);
        
        return "redirect:/"; // 회원 가입 성공 시 메인 페이지로 리다이렉트
    }
    
}