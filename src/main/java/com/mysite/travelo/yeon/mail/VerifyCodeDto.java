package com.mysite.travelo.yeon.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeDto {

	private String username;
    private String verifyCode;
	
}
