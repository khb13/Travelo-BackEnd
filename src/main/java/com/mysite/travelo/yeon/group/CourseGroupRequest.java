package com.mysite.travelo.yeon.group;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseGroupRequest {

	private Integer courseGroupSeq;
	
	@NotEmpty(message = "제목 필수 항목입니다")
	private String title;
	
	@NotEmpty(message = "코스 하나 이상 담아주세요")
	private List<CourseSeqRequest> courseSeqs; // 코스의 장소 목록

}
