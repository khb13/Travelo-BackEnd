package com.mysite.travelo.yeon.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

	// 자동 증가 값
	private Integer userSeq;
	
	@Email
	@NotEmpty(message = "이메일은 필수 항목입니다.")
	private String username;
	
	@Size(min = 8, max = 20, message = "비밀번호는 {min}자 이상 {max}자 이하로 입력해주세요.")
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$", 
             message = "비밀번호는 소문자영문과 숫자를 모두 포함해야 합니다.")
	private String password1;
	
    @NotEmpty(message = "비밀번호 확인 필수 항목입니다.")
	private String password2;
	
    @NotEmpty(message = "연락처 필수 항목입니다.")
	private String tel;
    
    public SiteUser toEntity() {
    	return SiteUser.builder()
    			.username(this.username)
    			.password(this.password1)
    			.tel(this.tel)
    			.role(UserRole.USER)
    			.build();
    }
}
