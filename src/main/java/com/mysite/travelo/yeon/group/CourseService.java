package com.mysite.travelo.yeon.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mysite.travelo.yeon.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	
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
	
}
