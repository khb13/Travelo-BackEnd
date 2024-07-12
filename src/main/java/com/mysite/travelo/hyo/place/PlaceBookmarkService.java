package com.mysite.travelo.hyo.place;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 장소 북마크 서비스
 * 1. addBookmark (int userSeq, int placeSeq) : 북마크에 추가하는 기능
 * 필요 값
 * : userSeq (사용자 순차 번호)
 * : placeSeq (장소 순차 번호)
 *
 * 2. removeBookmark (int userSeq, int placeSeq) : 북마크에서 삭제하는 기능
 * 필요 값
 * : userSeq (사용자 순차 번호)
 * : placeSeq (장소 순차 번호)
 *
 * 3.List<PlaceBookmark> getAllBookmarks(int userSeq) : 한 사용자의 북마크 내용을 전부 가져오는 기능
 * : userSeq (사용자 순차 번호)
 */

@RequiredArgsConstructor
@Service
public class PlaceBookmarkService {

    private final PlaceRepository placeRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;
    private final UserRepository userRepository;

    // 북마크 추가
    @Transactional
    public Map<String, Object> addBookmark (int userSeq, int placeSeq){
        SiteUser user = userRepository.findById(userSeq);
        Place place = placeRepository.findById(placeSeq);

        Map<String, Object> response = new HashMap<>();

        // 이미 북마크한 내용인 경우를 판단.
        if(!placeBookmarkRepository.existsByUserAndPlace(user,place)){
            PlaceBookmark placeBookmark = new PlaceBookmark();
            Set<SiteUser> userSet = new HashSet<>();
            userSet.add(user);
            placeBookmark.setUser(userSet);
            placeBookmark.setPlace(place);
            placeBookmarkRepository.save(placeBookmark);
            response.put("message", "북마크에 성공적으로 저장했습니다.");
        } else {
            response.put("message", "이미 북마크에 존재하는 내용입니다.");
        }
        return response;
    }

    // 북마크 내용 삭제
    @Transactional
    public Map<String, Object> removeBookmark(int userSeq, int placeSeq) {
        SiteUser user = userRepository.findById(userSeq);
        Place place = placeRepository.findById(placeSeq);

        Map<String, Object> response = new HashMap<>();

        // 북마크의 존재여부 확인.
        if (placeBookmarkRepository.existsByUserAndPlace(user, place)) {
            placeBookmarkRepository.deleteByUserAndPlace(user, place);
            response.put("message", "성공적으로 삭제했습니다.");
        } else {
            response.put("message", "처리 과정에서 문제가 발생했습니다. 내용이 존재하지 않습니다.");
        }

        return response;
    }

    // 북마크 불러오기?
    public List<PlaceBookmark> getAllBookmarks(int userSeq){
        SiteUser user = userRepository.findById(userSeq);
        return placeBookmarkRepository.findByUser(user);
    }
}
