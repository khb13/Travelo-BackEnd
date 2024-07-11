package com.mysite.travelo.hyo.customcourse;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.hyo.place.Place;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final CourseRepository courseRepository;
    private final CourseCustomService courseCustomService;


    // 테스트용 무인증.
    // request는 클라이언트에서 던져주는 값.
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCustomCourse(@Valid @RequestBody CustomCourseRequest request, BindingResult bindingResult){

        //response 생성
        Map<String, Object> response = new HashMap<>();

        // 장소 개수 검증
        if(request.getPlaceSeqs().isEmpty()) {
            response.put("lacksPlace","장소를 하나 이상 추가해주십시오.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(request.getPlaceSeqs().size() > 6) {
            response.put("overPlace", "장소는 최대 6개까지 추가할 수 있습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Valid 검사
        response = bindingResultError(bindingResult);

        // 실제 생성 서비스
        Map<String, Object> serviceResponse = courseCustomService.create(request);

        response.putAll(serviceResponse);

        // 결과값 반환
        if (response.containsKey("error")){
            // 에러 반환 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            // 성공 시
            return ResponseEntity.ok(response);
        }
    }

    // 코스 상세 내역 가져오기
    @GetMapping("/details/{courseSeq}")
    public ResponseEntity<Map<String, Object>> CustomCourseDetail(@PathVariable("courseSeq") Integer courseSeq){

        // 하나의 코스 정보를 가져오는 서비스
        Course course = courseService.getCourse(courseSeq);

        // 응답 response 생성
        Map<String, Object> response = new HashMap<>();
        response.put("course", course);

        return ResponseEntity.ok(response);
    }

    // 코스 수정
    @PostMapping("{courseSeq}/modify")
    public ResponseEntity<Map<String, Object>> modifyCustomCourse(@PathVariable("courseSeq") Integer courseSeq, @Valid @RequestBody CustomCourseRequest customCourseRequest, BindingResult bindingResult, Principal principal){

        // 코스 존재 여부 점검, 유효성 검사 진행
//        Map<String, Object> response = errorResponse(bindingResult,courseSeq, principal);

        Map<String, Object> response = new HashMap<>();
        Course course = courseService.getCourse(courseSeq);

        try {
            response = courseCustomService.modifiedCourse(courseSeq, customCourseRequest, response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    // 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCustomCourse(@Valid @RequestBody Map<String, Integer> courseSeq, BindingResult bindingResult, Principal principal){

        // 유효 여부 확인
//        Map<String, Object> response = errorResponse(bindingResult,courseSeq.get("courseSeq"), principal);

        // 결과에 따른 응답 메시지 반환
        Map<String, Object> response = new HashMap<>();
        response = courseCustomService.deleteCourse(courseSeq.get("courseSeq"), response);

        return ResponseEntity.ok(response);
    }

    // Map에 꽂을 핀
    @PostMapping("/mappin")
    public ResponseEntity<Map<String, Object>> mappinCourse(@Valid @RequestBody List<Place> placeList, BindingResult bindingResult){

        Map<String, Object> response = new HashMap<>();

        response = bindingResultError(bindingResult);
        if(response.containsKey("error")){
            return ResponseEntity.badRequest().body(response);
        }

        //결과 여부 반환.
        response = courseCustomService.mappin((placeList));

        // 결과 여부에 에러가 존재할 시,
        if(response.containsKey("error")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response);
    }

    // 에러 여부 확인용 유틸 메소드
    private Map<String, Object> errorResponse(BindingResult bindingResult, Integer courseSeq, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        response = bindingResultError(bindingResult);

        if (courseSeq == null || courseSeq < 1) {
            response.put("error", "해당하는 코스가 존재하지 않습니다.");
        }

        Course author = courseRepository.findByCourseSeq(courseSeq);
        if (author == null || !author.getUser().getUsername().equals(principal.getName())) {
            response.put("error", "권한이 없습니다.");
        }

        return response;
    }

    // bindingResultError 따로 빼냄.
    private Map<String, Object> bindingResultError(BindingResult bindingResult){
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return response;
    }

}

