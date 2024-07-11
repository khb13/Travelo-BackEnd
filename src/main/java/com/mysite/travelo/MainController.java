package com.mysite.travelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.travelo.hyo.place.Place;
import com.mysite.travelo.hyo.place.PlaceService;
import com.mysite.travelo.yeon.group.Course;
import com.mysite.travelo.yeon.group.CourseService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MainController {

	private final PlaceService placeService;
	private final CourseService courseService;
	
	@GetMapping("/")
	public ResponseEntity<?> index() {
		
		Map<String, Object> response = new HashMap<>(); 

		List<Map<String, String>> areaCodes = new ArrayList<>();
		
		String[] areaCodeStr = {"1", "3", "4", "5", "6", "39"};
		String[] areaNameStr = {"서울", "대전", "대구", "광주", "부산", "제주"};
		
		for (int i = 0; i < 6; i++) {
			Map<String, String> area = new HashMap<>();
			
			area.put("areaCode", areaCodeStr[i]);
			area.put("areaName", areaNameStr[i]);

			areaCodes.add(area);
		}
		
		response.put("areaCodes", areaCodes);
		
		List<Place> places = placeService.findPopularPlaces();
		if (places == null) {
			return new ResponseEntity<>("장소가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		response.put("places", places);
		
		List<Course> courses = courseService.findPopularCourses();
		if (courses == null) {
			return new ResponseEntity<>("코스가 없습니다", HttpStatus.NOT_FOUND);
		}
		
		response.put("courses", courses);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/admin")
    public String adminPage(Model model) {
		
        return "인가 성공!";
    }
	
}
