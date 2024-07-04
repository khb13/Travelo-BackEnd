package com.mysite.travelo.yeon.member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberCreateForm {

	// 자동 증가 값
	private Integer mSeq;
	
	private String username;
	
	@Size(min = 2, max = 20)
	@NotEmpty(message = "사용자 닉네임은 필수 항목입니다.")
	private String nickname;
	
}
