package com.mysite.travelo.yeon.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResignRequest {

	@NotEmpty(message = "비밀번호는 필수 항목입니다")
	private String password;
	 
	@NotEmpty(message = "비밀번호 확인 필수 항목입니다")
	private String passwordCheck;
	
}
