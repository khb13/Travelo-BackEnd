package com.mysite.travelo.gil.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;
import com.mysite.travelo.yeon.group.CourseSeqRequest;
import com.mysite.travelo.yeon.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	private final CourseLikeRepository courseLikeRepository;
	
//	코스 목록 불러오기(공개여부: 공개 / 정렬 디폴트값: 인기순 / 옵션값: 최신순, 오래된순)
	public Page<Course> getCourses(int page, String privateYn, String sortBy, String areaCode, String type) {
		
		Pageable pageable;
		
//		정렬
        if ("popularity".equals(sortBy)) {
            pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "viewCount"))); // 좋아요, 조회수 많은순
        } else if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "viewCount"))); // 좋아요, 조회수 많은순(디폴트)
        }
        
//      코스 목록
        if (areaCode != null && !areaCode.isEmpty() && type != null && !type.isEmpty()) {
        	// 지역과 장소유형 둘 다 존재하는 경우 해당 장소유형의 장소가 포함된 해당 지역의 코스를 반환
            return courseRepository.findAllByPrivateYnAndAreaCodeAndCourseListPlaceType(privateYn, areaCode, type, pageable);
            
        } else if (areaCode != null && !areaCode.isEmpty()) {
        	// 지역만 존재하는 경우 해당 지역의 코스를 반환
        	return courseRepository.findAllByPrivateYnAndAreaCode(privateYn, areaCode, pageable);
        	
        } else if (type != null && !type.isEmpty()) {
        	// 장소유형만 존재하는 경우 해당 유형의 장소가 포함된 코스를 반환
        	return courseRepository.findAllByPrivateYnAndCourseListPlaceType(privateYn, type, pageable);
        	
        } else {
        	// 그렇지 않은 경우 필터링 되지 않은 모든 코스를 반환
            return courseRepository.findAllByPrivateYn(privateYn, pageable);
        }
        
	}
	
//	코스 불러오기
	public Course getCourse(Integer courseSeq) {
		
		Optional<Course> oc = this.courseRepository.findById(courseSeq);
		if(oc.isPresent()) {
			return oc.get();
		} else {
			throw new DataNotFoundException("코스를 찾을 수 없습니다.");
		}
		
	}
	
//	코스 조회수 (증가)
	public Course increaseViewCount(Integer courseSeq) {
		
		Course course = courseRepository.findByCourseSeq(courseSeq);
		
		course.setViewCount(course.getViewCount() + 1);
		
		return courseRepository.save(course);
	}
	
//	좋아요 상태관리
	public String toggleCourseLike(Integer courseSeq, SiteUser user) {
		
		 // Course 엔티티를 가져옴
        Course course = courseRepository.findByCourseSeq(courseSeq);
        
        // 이미 좋아요가 존재하는지 확인
        Optional<CourseLike> ocl = courseLikeRepository.findByCourseAndAuthor(course, user);
		
        String likeYn;
        
	    if (ocl.isPresent()) {
	        CourseLike courseLike = ocl.get();
	        if ("Y".equals(courseLike.getLikeYn())) {
	            // 현재 좋아요 상태면, 좋아요 취소로 변경 및 좋아요 갯수 감소
	            courseLike.setLikeYn("N");
	            likeYn = "N";
	            decreaseLikeCount(courseSeq);
	        } else {
	            // 현재 좋아요 취소 상태면, 좋아요로 변경 및 좋아요 갯수 증가
	            courseLike.setLikeYn("Y");
	            likeYn = "Y";
	            increaseLikeCount(courseSeq);
	        }
	        courseLikeRepository.save(courseLike);
	    } else {
            // 좋아요가 존재하지 않을 경우 새로 추가
            CourseLike courseLike = new CourseLike();
            courseLike.setCourse(course);
            courseLike.setAuthor(user);
            courseLike.setLikeYn("Y");
            likeYn = "Y";
            courseLikeRepository.save(courseLike);
            increaseLikeCount(courseSeq);
        }
	    
	    return likeYn;
	}
	
//	코스 좋아요 수 증가
	public void increaseLikeCount(Integer courseSeq) {
	    Course course = courseRepository.findByCourseSeq(courseSeq);
	    
    	course.setLikeCount(course.getLikeCount() + 1);
	    
	    courseRepository.save(course);
	}
	
//	코스 좋아요 수 감소
	public void decreaseLikeCount(Integer courseSeq) {
	    Course course = courseRepository.findByCourseSeq(courseSeq);
	    
	    course.setLikeCount(course.getLikeCount() - 1);
	    
	    courseRepository.save(course);
	}
	
	public List<Course> getCustom(SiteUser user) {
		List<Course> list = courseRepository.findByAuthor(user);
		
		if (list.isEmpty() || list == null) {
			return null;
		}
		
		return list;
	}
	
	public List<Course> getCustomByArea(SiteUser user, String areaCode) {
		List<Course> list = courseRepository.findByAuthorAndAreaCode(user, areaCode);
		
		if (list.isEmpty() || list == null) {
			return null;
		}
		
		return list;
	}
	
	public boolean existCourse(List<CourseSeqRequest> courseSeqs) {
		
		for (CourseSeqRequest courseSeq : courseSeqs) {
			Optional<Course> oc = courseRepository.findById(courseSeq.getCourseSeq());
			
			if (oc.isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<Course> findPopularCourses() {
		
        Pageable pageable = PageRequest.of(0, 6); // 첫 번째 페이지에서 최대 6개의 결과를 가져오도록 설정
        
        return courseRepository.findAllByPrivateYnOrderByLikeCountDescViewCountDesc("N", pageable);
    }
	
	// 코스 목록 불러오기(정렬 디폴트값: 최신순 / 옵션값: 오래된순)
	public Page<Course> getAllCourse(int page, String sortBy) {
		
		Pageable pageable;
		
        if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
        }
        
    	return courseRepository.findAll(pageable);
	}
	
	// 코스 목록 불러오기(정렬 디폴트값: 최신순 / 옵션값: 오래된순)
	public Page<Course> getVisibleCourses(int page, String privateYn, String sortBy) {
		
		Pageable pageable;
		
        if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
        }
        
        return courseRepository.findAllByPrivateYn(privateYn, pageable);
	}

	public List<Course> getAllCourseCount() {
		
        return courseRepository.findAll();
	}
	
	public Page<Course> getAllCourseByUser(SiteUser author, int page, String sortBy) {
		
		Pageable pageable;
		
		if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        }
		
		return courseRepository.findByAuthor(author, pageable);
	}

	public void delete(Course course) {
		this.courseRepository.delete(course);
	}

}
