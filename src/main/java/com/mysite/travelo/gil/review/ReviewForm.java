package com.mysite.travelo.gil.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

	@NotBlank(message = "내용을 입력해주세요.") // 댓글 내용이 없거나 빈 문자열일 경우 비허용 및 오류 메세지
	@Size(max = 5000) // 댓글 글자의 갯수 5000자로 제한
	private String content;
}
