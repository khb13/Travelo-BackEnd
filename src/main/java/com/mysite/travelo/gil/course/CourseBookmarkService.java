package com.mysite.travelo.gil.course;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mysite.travelo.DataNotFoundException;
import com.mysite.travelo.yeon.user.User;
import com.mysite.travelo.yeon.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseBookmarkService {
	
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final CourseBookmarkRepository courseBookmarkRepository;

//	코스 북마크에 코스 추가
	public void addBookmark(Integer userSeq, Integer courseSeq) {
		
		Optional<User> ou = userRepository.findById(userSeq);
        User user;
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

        if (!courseBookmarkRepository.existsByUserAndCourse(user, course)) {
            CourseBookmark courseBookmark = new CourseBookmark();
            courseBookmark.setUser(user);
            courseBookmark.setCourse(course);
            courseBookmarkRepository.save(courseBookmark);
        }
	}
	
//	코스 북마크에서 북마크 삭제
	public void removeBookmark(Integer userSeq, Integer courseBookmarkSeq) {
		
		Optional<User> ou = userRepository.findById(userSeq);
        User user;
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
}
