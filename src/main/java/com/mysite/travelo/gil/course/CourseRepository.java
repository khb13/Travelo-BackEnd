package com.mysite.travelo.gil.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	
//	코스 전체보기 - 전체 지역(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	Page<Course> findAllByPrivateYn(String privateYN, Pageable pageable);
	
//	코스 전체보기 - 특정 지역(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	Page<Course> findAllByPrivateYnAndAreaCode(String privateYn, String areaCode, Pageable pageable);
	
//	코스 전체보기 - 특정 장소유형의 장소가 포함된 전체 지역(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	Page<Course> findAllByPrivateYnAndCourseListPlaceType(String privateYn, String type, Pageable pageable);
	
//	코스 전체보기 - 특정 장소유형의 장소가 포함된 특정 지역(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
    Page<Course> findAllByPrivateYnAndAreaCodeAndCourseListPlaceType(String privateYn, String areaCode, String type, Pageable pageable);
    
    
//  코스 상세보기
    Course findByCourseSeq(Integer courseSeq);
}
