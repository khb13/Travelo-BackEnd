package com.mysite.travelo.hyo.customcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseList;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.hyo.place.Place;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CourseCustomService {

    private final CourseListRepository courseListRepository;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> create(Map<String, Object> response) {
        Map<String, Object> result = new HashMap<>();

        try{
            Course course = new Course();

            //set 과정
            course.setTitle(convertToString(response.get("title")));
            course.setDescription(convertToString(response.get("description")));

            // placeMap을 CourseList 객체 리스트로 변환
            List<CourseList> courseLists = objectMapper.convertValue(response.get("placeMap"), objectMapper.getTypeFactory().constructCollectionType(List.class, CourseList.class));

            course.setCourseList(courseLists);
            course.setPrivateYN(convertToString(response.get("privateYN")));

            // 생성 시간 추가
            course.setCreateDate(LocalDateTime.now());


            // 코스 저장, 저장 결과 확인 위한 변수 할당
            Course savedCourse = this.courseRepository.save(course);

            // 코스리스트에 저장
            for(CourseList courseList : courseLists){
                courseList.setCourse(savedCourse);
            }
            courseListRepository.saveAll(courseLists);

            // 저장 후 결과 확인
            if(savedCourse != null && savedCourse.getCourseSeq() != null){
                // 성공
                result.put("message", "코스 생성을 성공했습니다.");
            } else {
                // 실패
                result.put("error", "코스 생성에 실패했습니다.");
            }
        } catch (Exception e){
            // 예외
            result.put("error", "Exception occurred: "+e.getMessage());
        }

        return result;
    }

    // 코스 수정
    @Transactional
    public Map<String, Object> modifiedCourse(Integer courseSeq, CustomCourseRequest customCourseRequest, Map<String, Object> response) {

        // 기존 코스를 가져오기.
        Course existingCourse = courseRepository.findByCourseSeq(courseSeq);

        /* *
         * 다음에 하나라도 해당될 경우 코스를 찾을 수 없다고 함.
         * 1. 기존 코스(existingCourse)가 null 인 경우
         * 2. existingCourse.getCourseSeq()와 받아온 courseSeq가 일치하지 않은 경우
         * 3. 기본 코스의 seq 값이 0보다 작을 경우.
         */
        if (existingCourse == null || !Objects.equals(existingCourse.getCourseSeq(), courseSeq) || existingCourse.getCourseSeq() < 0) {
            throw new IllegalArgumentException("코스를 찾을 수 없습니다.");
        }

        // 변경 사항 비교용 불린 변수
        boolean isModified = false;

        // 제목에 변경사항이 있는지 체크
        if(!existingCourse.getTitle().equals(customCourseRequest.getTitle())){
            existingCourse.setTitle(customCourseRequest.getTitle());
            isModified = true;
        }

        // 변경사항이 있는지, 디스크립션에 변경이 있는지 체크
        if(!isModified && !existingCourse.getDescription().equals(customCourseRequest.getDescription())){
            existingCourse.setDescription(customCourseRequest.getDescription());
            isModified = true;
        }

        // 변경사항이 있는지, 공개 비공개 여부에 변경이 있는지 체크
        if(!isModified && !existingCourse.getPrivateYN().equals(customCourseRequest.getPrivateYn())){
            existingCourse.setPrivateYN(customCourseRequest.getPrivateYn());
            isModified = true;
        }

        // 변경사항이 있었는지 체크
        if(!isModified){
            List<CourseList> courseLists = objectMapper.convertValue(customCourseRequest.getPlaceMap(), objectMapper.getTypeFactory().constructCollectionType(List.class, CourseList.class));
            // 변경사항이 없었을 경우, 코스 리스트에는 변경이 있는지 체크
            if(!existingCourse.getCourseList().equals(courseLists)){
                existingCourse.setCourseList(courseLists);
                isModified = true;
            }
        }

        // 변경사항이 있었을 경우, 수정일자 추가하고 내용 변경
        if(isModified) {
            existingCourse.setModifyDate(LocalDateTime.now());
            courseRepository.save(existingCourse);
            courseListRepository.saveAll(existingCourse.getCourseList());
            response.put("message", "코스 수정이 완료되었습니다.");
        } else {
            // 변경사항이 없었을 경우 변경사항이 없다는 메시지 반환.
            response.put("message", "변경사항이 없습니다.");
        }

        // response 메시지 반환
        return response;
    }

    // 코스 삭제
    public Map<String, Object> deleteCourse(Integer courseSeq, Map<String, Object> response) {

        // 삭제할 대상 가져오기
        Course deleteTarget = courseRepository.findByCourseSeq(courseSeq);

        // 삭제할 대상의 존재 여부 확인.
        if(deleteTarget == null || deleteTarget.getCourseSeq() == null){
            response.put("error", "삭제할 대상이 없습니다.");
        } else {
            this.courseRepository.delete(deleteTarget);
            response.put("message", "성공적으로 삭제하였습니다.");
        }

        return response;
    }

    //map에 pin 찍기 위해서 장소의 좌표값만 가져옴.
    @Transactional
    public Map<String, Object> mappin(List<Place> placeList){
        // 응답용 response 생성
        Map<String, Object> response = new HashMap<>();

        // 장소 최대 수 검사
        if(placeList.size()>6){
            response.put("error", "장소는 최대 6개까지 추가할 수 있습니다.");
            return response;
        }

        // 좌표와 장소번호를 저장할 placeMap 생성
        Map<Integer, Map<String, Double>> placeMap = new HashMap<>();

        // 받아온 placeList에 place가 존재하는 만큼 반복
        for (Place place : placeList) {
            // y, x 좌표를 각각 pin이라는 map에 저장
            Map<String, Double> pin = new HashMap<>();
            pin.put("Longitude", place.getLongitude());
            pin.put("latitude", place.getLatitude());
            // placeMap에 placeSeq와 pin을 저장
            placeMap.put(place.getPlaceSeq(), pin);
        }

        // response에 placeMap을 저장.
        // 여기서 response의 형태는 response<placeMap, <placeSeq, <longitude, latitude>>>
        response.put("placeMap", placeMap);
        return response;
    }

    // 안전한 String 변환을 위한 유틸리티 메서드
    private String convertToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}
