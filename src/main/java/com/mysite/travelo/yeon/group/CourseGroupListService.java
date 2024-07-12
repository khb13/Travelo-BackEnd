package com.mysite.travelo.yeon.group;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseGroupListService {

	@Autowired
	private final CourseGroupListRepository courseGroupListRepository;
	
	@Autowired
	private final CourseRepository courseRepository;
	
	@Transactional
	public void create(CourseGroup courseGroup, List<CourseSeqRequest> courseSeqs) {
		
		 for (CourseSeqRequest courseSeq : courseSeqs) {
			Optional<Course> oc = courseRepository.findById(courseSeq.getCourseSeq());
			
			if (oc.isPresent()) {
                Course course = oc.get();
                
                CourseGroupList courseGroupList = new CourseGroupList();
                courseGroupList.setCourseGroup(courseGroup);
                courseGroupList.setCourse(course);
                this.courseGroupListRepository.save(courseGroupList);
			}
			
		 }
		 
	}

	public boolean modify(CourseGroup courseGroup, List<CourseSeqRequest> courseSeqs) {
		
		List<CourseGroupList> currentGroupList = courseGroup.getCourseGroupList();

		// 새로운 코스 목록과 기존 목록이 동일한 경우 null 반환
	    if (isSameCourseList(currentGroupList, courseSeqs)) {
	        return true;
	    }
		
		// 새로운 코스 목록을 순회하면서 기존 목록과 비교하여 추가/삭제 처리
        for (CourseSeqRequest courseSeq : courseSeqs) {

        	// 기존에 있는지 확인
            boolean found = false;
            
            for (CourseGroupList groupList : currentGroupList) {
            	
                if (groupList.getCourse().getCourseSeq().equals(courseSeq.getCourseSeq())) {
                	found = true;
                    break;
                }
            }

            // 기존 목록에 없으면 추가
            if (!found) {
                Optional<Course> optionalCourse = courseRepository.findById(courseSeq.getCourseSeq());
                
                if (optionalCourse.isPresent()) {
                    Course course = optionalCourse.get();
                    
                    CourseGroupList newGroupList = new CourseGroupList();
                    newGroupList.setCourseGroup(courseGroup);
                    newGroupList.setCourse(course);
                    
                    courseGroupListRepository.save(newGroupList);
                }
            }
        }

        // 기존 목록에서 삭제할 코스 찾기
        for (CourseGroupList groupList : currentGroupList) {
            boolean found = false;
            
            for (CourseSeqRequest courseSeq : courseSeqs) {
                
            	if (groupList.getCourse().getCourseSeq().equals(courseSeq.getCourseSeq())) {
                    found = true;
                    break;
                }
            }

            // 새 목록에 없으면 삭제
            if (!found) {
                courseGroupListRepository.delete(groupList);
            }
        }
        
        return false;
		
	}
	
	// 기존 목록과 새로운 코스 목록이 동일한지 확인하는 메서드
	private boolean isSameCourseList(List<CourseGroupList> currentGroupList, List<CourseSeqRequest> courseSeqs) {
	    if (currentGroupList.size() != courseSeqs.size()) {
	    	
	        return false;
	    }

	    for (CourseGroupList groupList : currentGroupList) {
	        boolean found = false;
	        
	        for (CourseSeqRequest courseSeq : courseSeqs) {
	        	
	            if (groupList.getCourse().getCourseSeq().equals(courseSeq.getCourseSeq())) {
	                found = true;
	                break;
	            }
	        }
	        
	        if (!found) {
	        	
	            return false;
	        }
	    }
	    
	    return true;
	}
	
}
