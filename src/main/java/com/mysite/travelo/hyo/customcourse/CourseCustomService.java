package com.mysite.travelo.hyo.customcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseList;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.gil.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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


}
