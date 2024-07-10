package com.mysite.travelo.yeon.group;

import java.util.List;
import java.util.Optional;

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
	
}
