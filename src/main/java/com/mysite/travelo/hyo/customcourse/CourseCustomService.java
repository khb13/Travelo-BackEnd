package com.mysite.travelo.hyo.customcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseList;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.gil.course.CourseService;
import com.mysite.travelo.hyo.place.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CourseCustomService {

    private final CourseListRepository courseListRepository;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;


    public void create(Map<String, Object> response) {
        Course course = new Course();
        course.setTitle(convertToString(response.get("title")));
        course.setDescription(convertToString(response.get("description").toString()));

        List<CourseList> courseLists = objectMapper.convertValue(response.get("placeMap"), objectMapper.getTypeFactory().constructCollectionType(List.class, CourseList.class));

        course.setCourseList(courseLists);
        course.setPrivateYN(convertToString(response.get("privateYN").toString()));
        this.courseRepository.save(course);
    }

    // 안전한 String 변환을 위한 유틸리티 메서드
    private String convertToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    public void modifiedCourse(Integer courseSeq, CustomCourseRequest customCourseRequest) {
        Course course = new Course();
        course.setTitle(customCourseRequest.getTitle());
        course.setDescription(customCourseRequest.getDescription());
        course.setPrivateYN(customCourseRequest.getPrivateYn());

        List<CourseList> courseLists = List.of((CourseList) customCourseRequest.getPlaceMap());

        course.setCourseList(courseLists);

        Course existingCourse = this.courseRepository.findByCourseSeq(courseSeq);
        if (existingCourse == null) {
            throw new IllegalArgumentException("Course not found");
        }

        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setPrivateYN(course.getPrivateYN());
        existingCourse.setCourseList(course.getCourseList());
        existingCourse.setCourseSeq(course.getCourseSeq());

        this.courseRepository.save(existingCourse);
    }

    public void deleteCourse(Integer courseSeq) {
        this.courseRepository.delete(this.courseRepository.findByCourseSeq(courseSeq));

    }

    public Map<String, Object> mappin(List<Place> placeList){
        Map<String, Object> response = new HashMap<>();
        if(placeList.size()>6){
            response.put("error", "장소는 최대 6개까지 추가할 수 있습니다.");
            return response;
        }



        Map<Integer, Map<String, Double>> placeMap = new HashMap<>();
        for (Place place : placeList) {
            Map<String, Double> pin = new HashMap<>();
            pin.put("Longitude", place.getLongitude());
            pin.put("latitude", place.getLatitude());
            placeMap.put(place.getPlaceSeq(), pin);
        }

        response.put("placeMap", placeMap);
        return response;
    }


}
