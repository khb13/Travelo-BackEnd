package com.mysite.travelo.yeon.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckRequest {

	@Email
	@NotEmpty(message = "이메일은 필수 항목입니다")
	private String username;
	
    @NotEmpty(message = "비밀번호는 필수 항목입니다")
	private String password;
	
}
