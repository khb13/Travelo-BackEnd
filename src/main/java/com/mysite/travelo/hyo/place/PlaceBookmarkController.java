package com.mysite.travelo.hyo.place;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.mysite.travelo.yeon.user.SiteUser;
import com.mysite.travelo.yeon.user.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 장소 북마크는 별개로 빼냈습니다.
 * addBookmark : 북마크에 집어넣는 기능
 * 필요값
 * : userSeq (사용자 순차 번호)
 * : placeSeq (장소 순차 번호)
 *
 * removeBookmark : 북마크에서 삭제하는 기능
 * 필요값
 * : userSeq (사용자 순차 번호)iiiiiiiiiii
 * : placeSeq (장소 순차 번호)
 *
 * getPlaceBookmarks : 해당 유저의 모든 북마크 목록을 가져오는 기능
 * 필요값
 * : userSeq (사용자 순차 번호)
 *
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/placebookmarks")
@CrossOrigin(origins="http://localhost:5173")
public class PlaceBookmarkController {

	private final UserService userService;
    private final PlaceBookmarkService placeBookmarkService;

    // 북마크 추가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBookmark(Authentication auth, @RequestParam int placeSeq) {
        Map<String, Object> response;
        
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        response = placeBookmarkService.addBookmark(loginUser, placeSeq);
        return ResponseEntity.ok(response);
    }

    // 북마크 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeBookmark(Authentication auth, @RequestParam int placeSeq) {
        Map<String, Object> response;
        
        SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
        
        response = placeBookmarkService.removeBookmark(loginUser, placeSeq);
        return ResponseEntity.ok(response);
    }

    // 모든 북마크를 가져오는 기능.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity<List<PlaceBookmark>> getPlaceBookmarks(Authentication auth) {
    	
    	SiteUser loginUser = userService.getLoginUserByUsername(auth.getName());
    	
        List<PlaceBookmark> bookmarks = placeBookmarkService.getAllBookmarks(loginUser);
        return ResponseEntity.ok(bookmarks);
    }
}
