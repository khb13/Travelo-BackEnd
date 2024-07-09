package com.mysite.travelo.gil.course;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
	
	private final CourseService courseService;

//	코스 전체보기(정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	@GetMapping("/list")
	public String list(Model model,
						@RequestParam(value="page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "popularity") String sortBy,
						@RequestParam(value = "areaCode", defaultValue = "") String areaCode,
						@RequestParam(value = "type", defaultValue = "") String type) {
		
		Page<Course> paging = courseService.getList(page, "N", sortBy, areaCode, type);
	    
        model.addAttribute("paging", paging);
		
		return "course/List";
	}
	
//	코스 상세보기
	@GetMapping("/detail/{courseSeq}")
	public String detail(Model model,
						@PathVariable("courseSeq") Integer courseSeq) {
		
		Course course = courseService.getCourse(courseSeq);
		
		model.addAttribute("course", course);
		
		return "course/detail";
	}
	
}
