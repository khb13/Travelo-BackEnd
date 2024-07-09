package com.mysite.travelo.yeon.group;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.travelo.yeon.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseGroupService {

	private final CourseGroupRepository courseGroupRepository;
	
	public List<CourseGroup> getList(SiteUser loginUser) {
		
		List<CourseGroup> list = courseGroupRepository.findByAuthorUsername(loginUser.getUsername());
		
		if (list.isEmpty()) {
			return null;
		}
		
		return list;
	}
	
	public CourseGroup getCourse(Integer courseGroupSeq) {
		
		Optional<CourseGroup> list = courseGroupRepository.findById(courseGroupSeq);
		
		if (list.isEmpty()) {
			return null;
		}
		
		return list.get();
	}
	
	public void delete(CourseGroup courseGroup) {
		
		this.courseGroupRepository.delete(courseGroup);
	}
	
}
