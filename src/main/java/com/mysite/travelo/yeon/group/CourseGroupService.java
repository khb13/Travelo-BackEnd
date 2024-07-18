package com.mysite.travelo.yeon.group;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	
	public CourseGroup create(CourseGroupRequest courseGroupRequest, SiteUser loginUser) {
		
		CourseGroup courseGroup = new CourseGroup();
		
		courseGroup.setTitle(courseGroupRequest.getTitle());
		courseGroup.setAuthor(loginUser);
		courseGroup.setCreateDate(LocalDateTime.now());
		
		this.courseGroupRepository.save(courseGroup);
		
		return courseGroup;
	}
	
	public CourseGroup modify(CourseGroupRequest courseGroupRequest, SiteUser loginUser, Integer courseGroupSeq) {
		
		Optional<CourseGroup> ocg = courseGroupRepository.findById(courseGroupSeq);
		
		if (ocg.isEmpty()) {
			return null;
		}
		
		CourseGroup courseGroup = ocg.get();
		
		courseGroup.setTitle(courseGroupRequest.getTitle());
		courseGroup.setModifyDate(LocalDateTime.now());
		
		this.courseGroupRepository.save(courseGroup);
		
		return courseGroup;
	}
	
	public Page<CourseGroup> getAllGroup(int page, String sortBy) {
		
		Pageable pageable;
		
        if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
        }
		
        return courseGroupRepository.findAll(pageable);
	}
	
	public List<CourseGroup> getAllGroupCount() {
		
		return courseGroupRepository.findAll();
	}
	
	public Page<CourseGroup> getAllGroupByUser(String username, int page, String sortBy) {
		
		Pageable pageable;
		
		if ("latest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순
        } else if ("oldest".equals(sortBy)) {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "createDate")); // 오래된순
        } else {
        	pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createDate")); // 최신순(디폴트)
        }
		
		return courseGroupRepository.findByAuthorUsername(username, pageable);
	}
	
}
