package com.mysite.travelo.gil.course;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;
import com.mysite.travelo.gil.review.ReviewReport;
import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseBookmarkService {
	
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final CourseBookmarkRepository courseBookmarkRepository;

//	코스 북마크에 코스 추가
	public String addBookmark(Integer userSeq, Integer courseSeq) {
		
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		SiteUser user;
        if (ou.isPresent()) {
            user = ou.get();
        } else {
            throw new DataNotFoundException("회원을 찾을 수 없습니다.");
        }

        Optional<Course> oc = courseRepository.findById(courseSeq);
        Course course;
        if (oc.isPresent()) {
            course = oc.get();
        } else {
            throw new DataNotFoundException("코스를 찾을 수 없습니다.");
        }

     // 북마크가 존재하는지 확인
        boolean cb = courseBookmarkRepository.existsByUserAndCourse(user, course);
        
        if (cb) {
            return "이미 북마크한 코스입니다.";
        } else {
            // 북마크가 존재하지 않을 경우 새로 추가
        	CourseBookmark courseBookmark = new CourseBookmark();
            courseBookmark.setUser(user);
            courseBookmark.setCourse(course);
            courseBookmarkRepository.save(courseBookmark);

            return "북마크가 추가되었습니다.";
        }
	}
	
//	코스 북마크에서 북마크 삭제
	public void removeBookmark(Integer userSeq, Integer courseBookmarkSeq) {
		
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		SiteUser user;
        if (ou.isPresent()) {
            user = ou.get();
        } else {
            throw new DataNotFoundException("회원을 찾을 수 없습니다.");
        }

        Optional<CourseBookmark> ocb = courseBookmarkRepository.findByUserAndCourseBookmarkSeq(user, courseBookmarkSeq);
        CourseBookmark courseBookmark;
        if (ocb.isPresent()) {
            courseBookmark = ocb.get();
            courseBookmarkRepository.delete(courseBookmark);
        } else {
            throw new DataNotFoundException("북마크를 찾을 수 없습니다.");
        }
		
	}
	
	public List<CourseBookmark> getList(Integer userSeq) {
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		
		if (ou.isEmpty()) {
			return null;
		}
		
		return courseBookmarkRepository.findByUser(ou.get());
	}
	
	public List<CourseBookmark> getListByArea(Integer userSeq, String areaCode) {
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		
		if (ou.isEmpty()) {
			return null;
		}
		
		List<CourseBookmark> list = courseBookmarkRepository.findByUserAndCourse_AreaCode(ou.get(), areaCode);
		
		if (list.isEmpty() || list == null) {
			return null;
		}
		
		return list;
	}

	public void delete(Course course) {
		
		List<CourseBookmark> bookmarks = courseBookmarkRepository.findByCourse(course);

		for (CourseBookmark b : bookmarks) {
			this.courseBookmarkRepository.delete(b);
		}
		
	}
	
}
