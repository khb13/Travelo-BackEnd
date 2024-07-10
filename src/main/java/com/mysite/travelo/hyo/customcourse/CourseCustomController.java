package com.mysite.travelo.hyo.customcourse;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseList;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.hyo.place.Place;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/custom")
@RestController
public class CourseCustomController {

    private final CourseService courseService;
    private final CourseListRepository courseListRepository;
    private final CourseRepository courseRepository;
    private final CourseCustomService courseCustomService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCustomCourse(@Valid @RequestBody CustomCourseRequest customCourseRequest, BindingResult bindingResult){

        Map<String, Object> response = new HashMap<>();

        if(customCourseRequest.getPlaceMap().isEmpty()) {
            response.put("lacksPlace","장소를 하나 이상 추가해주십시오.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(customCourseRequest.getPlaceMap().size()>6) {
            response.put("overPlace", "장소는 최대 6개까지 추가할 수 있습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("title", customCourseRequest.getTitle());
        response.put("description", customCourseRequest.getDescription());
        response.put("placeList", customCourseRequest.getPlaceMap());
        response.put("privateYn", customCourseRequest.getPrivateYn());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{courseSeq}")
    public ResponseEntity<Map<String, Object>> CustomCourseDetail(@PathVariable("courseSeq") Integer courseSeq){

        Course course = courseService.getCourse(courseSeq);

        Map<String, Object> response = new HashMap<>();
        response.put("course", course);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/modified")
    public ResponseEntity<Map<String, Object>> modifiedCustomCourse(@Valid @RequestBody Integer courseSeq, @Valid @RequestBody CustomCourseRequest customCourseRequest, BindingResult bindingResult, Principal principal){
        System.out.println("Received modified data:");
        System.out.println("Title: " + customCourseRequest.getTitle());
        System.out.println("Description: " + customCourseRequest.getDescription());
        System.out.println("Private: " + customCourseRequest.getPrivateYn());
        System.out.println("Place List: " + customCourseRequest.getPlaceMap());



        Map<String, Object> response = new HashMap<>();

        Course author = courseRepository.findByCourseSeq(courseSeq);

        if(bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        if(courseSeq == null || courseSeq < 1) {
            response.put("error", "해당하는 코스가 존재하지 않습니다.");
        }

        if(!author.getUser().getUsername().equals(principal.getName())){
            response.put("error", "권한이 업습니다.");
        }

        courseCustomService.modifiedCourse(courseSeq,  customCourseRequest);

        response.put("message", "Course modified successfully.");

        return ResponseEntity.ok(response);

    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCustomCourse(@Valid @RequestBody Integer courseSeq, BindingResult bindingResult, Principal principal){

        Map<String, Object> response = new HashMap<>();

        Course author = courseRepository.findByCourseSeq(courseSeq);

        if(bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        if(courseSeq == null || courseSeq < 1) {
            response.put("error", "해당하는 코스가 존재하지 않습니다.");
        }

        if(!author.getUser().getUsername().equals(principal.getName())){
            response.put("error", "권한이 업습니다.");
        }

        courseCustomService.deleteCourse(courseSeq);

        response.put("message", "Course deleted successfully.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mappin")
    public ResponseEntity<Map<String, Object>> mappinCourse(@Valid @RequestBody List<Place> placeList, BindingResult bindingResult){

        Map<String, Object> response = courseCustomService.mappin((placeList));

        if(bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        if(response.containsKey("error")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response);
    }

}

