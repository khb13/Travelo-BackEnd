package com.mysite.travelo.hyo.customcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseList;
import com.mysite.travelo.gil.course.CourseRepository;
import com.mysite.travelo.hyo.place.Place;
import com.mysite.travelo.hyo.place.PlaceRepository;
import com.mysite.travelo.yeon.user.UserRepository;
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
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

//    @Transactional
//    public Map<String, Object> create(Map<String, Object> response) {
//        Map<String, Object> result = new HashMap<>();
//
//        try{
//            Course course = new Course();
//
//            //set 과정
//            course.setTitle(convertToString(response.get("title")));
//            course.setDescription(convertToString(response.get("description")));
//
//            // placeMap을 CourseList 객체 리스트로 변환
//            List<CourseList> courseLists = objectMapper.convertValue(response.get("placeMap"), objectMapper.getTypeFactory().constructCollectionType(List.class, CourseList.class));
//
//            course.setCourseList(courseLists);
//            course.setPrivateYN(convertToString(response.get("privateYN")));
//
//            // 생성 시간 추가
//            course.setCreateDate(LocalDateTime.now());
//
//
//            // 코스 저장, 저장 결과 확인 위한 변수 할당
//            Course savedCourse = this.courseRepository.save(course);
//
//            // 코스리스트에 저장
//            for(CourseList courseList : courseLists){
//                courseList.setCourse(savedCourse);
//            }
//            courseListRepository.saveAll(courseLists);
//
//            // 저장 후 결과 확인
//            if(savedCourse != null && savedCourse.getCourseSeq() != null){
//                // 성공
//                result.put("message", "코스 생성을 성공했습니다.");
//            } else {
//                // 실패
//                result.put("error", "코스 생성에 실패했습니다.");
//            }
//        } catch (Exception e){
//            // 예외
//            result.put("error", "Exception occurred: "+e.getMessage());
//        }
//
//        return result;
//    }

//    @Transactional
//    public void create(CustomCourseRequest request) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication == null || !authentication.isAuthenticated()) {
//            throw new RuntimeException("User is not authenticated");
//        }
//
//        Object principal = authentication.getPrincipal();
//        int userSeq;
//        if(principal instanceof User) {
//            userSeq = ((User) principal).getUserSeq();
//        } else {
//            throw new RuntimeException("User is not authenticated");
//        }
//
//        // Course 생성
//        Course course = new Course();
//        course.setTitle(request.getTitle());
//        course.setDescription(request.getDescription());
//        course.setUser(userRepository.findById(userSeq));
//        course.setCreateDate(LocalDateTime.now());
//        course.setPrivateYN(request.getPrivateYn());
//        courseRepository.save(course);
//
//        // CourseList 생성
//        for (CustomCourseRequest.Place place : request.getPlaceMap().values()) {
//            CourseList courseList = new CourseList();
//            courseList.setCourse(course);
//            courseList.setPlace(placeRepository.findById(place.getPlaceSeq()).orElseThrow(() -> new RuntimeException("Place not found")));
//            courseListRepository.save(courseList);
//        }
//    }

    // 생성, Create
    // 이후에 userSeq 변경 필요.
    @Transactional
    public Map<String, Object> create(CustomCourseRequest request) {
        // 예제로 사용할 회원 번호
        int userSeq = 1; // 여기에 실제 회원 번호를 넣어야 합니다.

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication == null || !authentication.isAuthenticated()) {
//            throw new RuntimeException("User is not authenticated");
//        }
//
//        Object principal = authentication.getPrincipal();
//        int userSeq;
//        if(principal instanceof User) {
//            userSeq = ((User) principal).getUserSeq();
//        } else {
//            throw new RuntimeException("User is not authenticated");
//        }

        // 응답 반환용 response map.
        Map<String, Object> response = new HashMap<>();

        // Course 생성
        Course course = new Course();

        // 이미 받아온 request에 임시 저장된 값들을 불러서, 실제 DB에 저장하기

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        // 현재 사용자의 userSeq를 불러옴.
        // 이후 이건 Authentication으로 변경.
        course.setUser(userRepository.findById(userSeq));
        course.setPrivateYN(request.getPrivateYn());

        // 초기화용 set 데이터
        course.setCreateDate(LocalDateTime.now());
        course.setViewCount(0);
        course.setLikeCount(0);

        List<CourseList> courseLists = new ArrayList<>();

        CourseList courseList = new CourseList();
        List<Place> placeList = new ArrayList<>();

        // Course에 들어가는 PlaceList 생성
        for (int placeSeq : request.getPlaceSeqs()) {
            Place place = placeRepository.findById(placeSeq);

            // 장소 유효 여부 검사
            if(place == null) {
                throw new IllegalArgumentException("장소가 유효하지 않습니다.");
            }

            Optional<CourseList> optionalCourseList = courseListRepository.findById(placeSeq);
            if(optionalCourseList.isPresent()) {
                CourseList coursePlaceList = optionalCourseList.get();
                coursePlaceList.setCourse(course);
                courseLists.add(coursePlaceList);
            }

            //placeList에 place들을 저장함.
            //즉, 리스트에 장소를 저장함.
            placeList.add(place);
            courseList.setPlace(place);
            // courseList에 해당 장소 내용들을 저장함.
            // 이 과정에서 동시에 AreaCode를 가져와서 set함.
            course.setAreaCode(place.getAreaCode());
            response.put("message", "코스 장소 목록 저장 성공.");
            response.put("placeList", placeList);
        }
        courseList.setCourse(course);
        this.courseListRepository.save(courseList);
        request.setCourse(course);

        course.setCourseList(courseLists);

        // 장소 리스트를 request에도 set해줌.
        courseRepository.save(course);
        response.put("message", "코스 저장 성공.");
        response.put("course", course);

        return response;
    }

            // 코스 수정
    @Transactional
    public Map<String, Object> modifiedCourse(Integer courseSeq, CustomCourseRequest customCourseRequest, Map<String, Object> response) {

        /* *
         * 다음에 하나라도 해당될 경우 코스를 찾을 수 없다고 함.
         * 1. 기존 코스(currentCourse)가 null 인 경우
         * 2. existingCourse.getCourseSeq()와 받아온 courseSeq가 일치하지 않은 경우
         * 3. 기본 코스의 seq 값이 0보다 작을 경우.
         */

        // 기존 코스를 가져오기.
        // courseSeq를 입력 받아서 그에 알맞는 기존 코스(수정 대상)를 가져옴.
        Course currentCourse = courseRepository.findByCourseSeq(courseSeq);

        if (currentCourse == null) {
            throw new IllegalArgumentException("코스를 찾을 수 없습니다.");
        }

        // 변경 사항 비교용 불린 변수
        boolean isModified = false;

        // 제목에 변경사항이 있는지 체크
        if(!currentCourse.getTitle().equals(customCourseRequest.getTitle())){
            currentCourse.setTitle(customCourseRequest.getTitle());
            isModified = true;
            response.put("modyfiedTitle", currentCourse.getTitle());
        }

        // 변경사항이 있는지, 디스크립션에 변경이 있는지 체크
        if(!currentCourse.getDescription().equals(customCourseRequest.getDescription())){
            currentCourse.setDescription(customCourseRequest.getDescription());
            isModified = true;
            response.put("modyfiedDescription", currentCourse.getDescription());
        }

        // 변경사항이 있는지, 공개 비공개 여부에 변경이 있는지 체크
        if(!currentCourse.getPrivateYN().equals(customCourseRequest.getPrivateYn())){
            currentCourse.setPrivateYN(customCourseRequest.getPrivateYn());
            isModified = true;
            response.put("modyfiedPrivate", currentCourse.getPrivateYN());
        }

        // 변경사항이 있었는지 체크

        List<CourseList> courseLists = objectMapper.convertValue(customCourseRequest.getPlaceSeqs(), objectMapper.getTypeFactory().constructCollectionType(List.class, CourseList.class));

        List<CourseList> existingLists = currentCourse.getCourseList();

            // 변경사항이 없었을 경우, 코스 리스트에는 변경이 있는지 체크
            if(!existingLists.equals(courseLists)) {
                for (CourseList courseList : courseLists) {

                    currentCourse.setCourseList(courseLists);
                    isModified = true;
                    response.put("modyfiedList", currentCourse.getCourseList());
                }
            }

//        if (existingCourse == null || !Objects.equals(existingCourse.getCourseSeq(), courseSeq) || existingCourse.getCourseSeq() < 0) {
//            throw new IllegalArgumentException("코스를 찾을 수 없습니다.");
//        }
//
//        if(existingCourse == null || existingCourse.getCourseList().isEmpty()) {
//            throw new IllegalArgumentException("코스는 비어있을 수 없습니다.");
//        }
//
//        for(CourseList place : existingCourse.getCourseList()) {
//            if(place == null){
//                throw new IllegalArgumentException("각각의 내용은 비어있을 수 없습니다.");
//            }
//        }


        // 변경사항이 있었을 경우, 수정일자 추가하고 내용 변경
        if(isModified) {
            currentCourse.setModifyDate(LocalDateTime.now());
            courseRepository.save(currentCourse);
            courseListRepository.saveAll(currentCourse.getCourseList());
            response.put("message", "코스 수정이 완료되었습니다.");
            response.put("변경 후: ", currentCourse);
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
