package com.mysite.travelo.yeon.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
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
    
}