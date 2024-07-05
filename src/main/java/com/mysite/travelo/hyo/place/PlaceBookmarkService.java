package com.mysite.travelo.hyo.place;

import com.mysite.travelo.yeon.user.User;
import com.mysite.travelo.yeon.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

//@RequiredArgsConstructor
@Service
public class PlaceBookmarkService {

    @Autowired
    private final PlaceRepository placeRepository;
    @Autowired
    private final PlaceBookmarkRepository placeBookmarkRepository;
    @Autowired
    private final UserRepository userRepository;

    public PlaceBookmarkService(PlaceBookmarkRepository placeBookmarkRepository, PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeBookmarkRepository = placeBookmarkRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    public void addBookmark (int userSeq, int placeSeq){
        User user = userRepository.findById(userSeq);
        Place place = placeRepository.findById(placeSeq);

        if(!placeBookmarkRepository.existsByUserAndPlace(user,place)){
            PlaceBookmark placeBookmark = new PlaceBookmark();
            Set<User> userSet = new HashSet<>();
            userSet.add(user);
            placeBookmark.setUser(userSet);
            placeBookmark.setPlace(place);
            placeBookmarkRepository.save(placeBookmark);
        }
    }

    @Transactional
    public void removeBookmark(int userSeq, int placeSeq) {
        User user = userRepository.findById(userSeq);
        Place place = placeRepository.findById(placeSeq);

        if (placeBookmarkRepository.existsByUserAndPlace(user, place)) {
            placeBookmarkRepository.deleteByUserAndPlace(user, place);
        }
    }

    public List<PlaceBookmark> getAllBookmarks(int userSeq){
        User user = userRepository.findById(userSeq);
        return placeBookmarkRepository.findByUser(user);
    }

}
