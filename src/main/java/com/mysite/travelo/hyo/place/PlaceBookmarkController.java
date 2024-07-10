package com.mysite.travelo.hyo.place;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
 * : userSeq (사용자 순차 번호)
 * : placeSeq (장소 순차 번호)
 *
 * getPlaceBookmarks : 해당 유저의 모든 북마크 목록을 가져오는 기능
 * 필요값
 * : userSeq (사용자 순차 번호)
 *
 */

@RequiredArgsConstructor
@RequestMapping("/placebookmarks")
@Controller
public class PlaceBookmarkController {

    private final PlaceBookmarkService placeBookmarkService;

    // 북마크 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBookmark(@RequestParam int userSeq, @RequestParam int placeSeq) {
        Map<String, Object> response;
        response = placeBookmarkService.addBookmark(userSeq, placeSeq);
        return ResponseEntity.ok(response);
    }

    // 북마크 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeBookmark(@RequestParam int userSeq, @RequestParam int placeSeq) {
        Map<String, Object> response;
        response = placeBookmarkService.removeBookmark(userSeq, placeSeq);
        return ResponseEntity.ok(response);
    }

    // 모든 북마크를 가져오는 기능.
    @GetMapping("/all")
    public ResponseEntity<List<PlaceBookmark>> getPlaceBookmarks(@RequestParam int userSeq) {
        List<PlaceBookmark> bookmarks = placeBookmarkService.getAllBookmarks(userSeq);
        return ResponseEntity.ok(bookmarks);
    }
}
