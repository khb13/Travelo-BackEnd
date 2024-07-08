package com.mysite.travelo.gil.course;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	
//	코스 목록 불러오기(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	public Page<Course> getList(int page, String privateYN, String sortBy, String areaCode, String type) {
		
		Pageable pageable;
		
//		정렬
        if ("popularity".equals(sortBy)) {
            pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "viewCount"))); // 좋아요, 조회수 많은순
        } else if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "viewCount"))); // 좋아요, 조회수 많은순(디폴트)
        }
        
//      코스 목록
        if (areaCode != null && !areaCode.isEmpty() && type != null && !type.isEmpty()) {
        	// 지역과 장소유형 둘 다 존재하는 경우 해당 장소유형의 장소가 포함된 해당 지역의 코스를 반환
            return courseRepository.findAllByPrivateYNAndAreaCodeAndCourseListPlaceType(privateYN, areaCode, type, pageable);
            
        } else if (areaCode != null && !areaCode.isEmpty()) {
        	// 지역만 존재하는 경우 해당 지역의 코스를 반환
        	return courseRepository.findAllByPrivateYNAndAreaCode(privateYN, areaCode, pageable);
        	
        } else if (type != null && !type.isEmpty()) {
        	// 장소유형만 존재하는 경우 해당 유형의 장소가 포함된 코스를 반환
        	return courseRepository.findAllByPrivateYNAndCourseListPlaceType(privateYN, type, pageable);
        	
        } else {
        	// 그렇지 않은 경우 필터링 되지 않은 모든 코스를 반환
            return courseRepository.findAllByPrivateYN(privateYN, pageable);
        }
        
	}
	
//	코스 불러오기
	public Course getCourse(Integer courseSeq) {
		
		Optional<Course> oc = this.courseRepository.findById(courseSeq);
		if(oc.isPresent()) {
			return oc.get();
		} else {
			throw new DataNotFoundException("코스가 존재하지 않습니다.");
		}
		
	}

}
