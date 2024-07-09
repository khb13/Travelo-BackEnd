package com.mysite.travelo.yeon.group;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseBookmarkService {

	private final UserRepository userRepository;
	private final CourseBookmarkRepository bookmarkRepository;
	
	public List<CourseBookmark> getList(Integer userSeq) {
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		
		if (ou.isEmpty()) {
			return null;
		}
		
		return bookmarkRepository.findByUser(ou.get());
	}
	
	public List<CourseBookmark> getListByArea(Integer userSeq, String areaCode) {
		Optional<SiteUser> ou = userRepository.findById(userSeq);
		
		if (ou.isEmpty()) {
			return null;
		}
		
		List<CourseBookmark> list = bookmarkRepository.findByUserAndCourse_AreaCode(ou.get(), areaCode);
		
		if (list.isEmpty() || list == null) {
			return null;
		}
		
		return list;
	}
	
}
