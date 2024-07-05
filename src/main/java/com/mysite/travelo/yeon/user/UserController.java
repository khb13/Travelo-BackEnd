package com.mysite.travelo.yeon.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JWTUtil jwtUtil;
	
	/*
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "yeon/signup/form"; 
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "yeon/signup/form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
			return "yeon/signup/form";  
		}
        
        Map<String, String> map = new HashMap<>();
        map.put("username", userCreateForm.getUsername());
        map.put("password", userCreateForm.getPassword1());
        map.put("tel", userCreateForm.getTel());

        try {
        	userService.create(map);

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");  
            return "yeon/signup/form"; 

        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage()); 
            return "yeon/signup/form"; 
        }

        return "redirect:/user/login"; 
    }
    
    @GetMapping("/login")
    public String login() {
        return "yeon/login/form";
    }
    
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/mypage")
	public String mypage() {
		return "yeon/mypage";
	}
    
    /**/
	
	@GetMapping("/join")
    public String joinPage(Model model) {

        // 회원가입을 위해서 model 통해서 joinRequest 전달
        model.addAttribute("joinRequest", new JoinRequest());
        return "/yeon/join/form";
    }
	
	@PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest,
                       BindingResult bindingResult) {

        // ID 중복 여부 확인
        if (userService.checkUsernameDuplicate(joinRequest.getUsername())) {
            return "ID가 존재합니다.";
        }


        // 비밀번호 = 비밀번호 체크 여부 확인
        if (!joinRequest.getPassword1().equals(joinRequest.getPassword2())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        // 에러가 존재하지 않을 시 joinRequest 통해서 회원가입 완료
        userService.securityJoin(joinRequest);

        // 회원가입 시 홈 화면으로 이동
        return "redirect:/";
    }
	
	@PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){

        SiteUser user = userService.login(loginRequest);


        if(user==null){
            return "ID 또는 비밀번호가 일치하지 않습니다!";
        }

        String token = jwtUtil.createJwt(user.getUsername(), user.getRole().toString(), 1000 * 60 * 60L);
        return token;
    }

	@GetMapping("/info")
    public String memberInfo(Authentication auth, Model model) {

		SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());

        return "ID : " + loginUser.getUsername() + "\nrole : " + loginUser.getRole();
    }
    
    @GetMapping("/admin")
    public String adminPage(Model model) {

        return "인가 성공!";
    }
	
}