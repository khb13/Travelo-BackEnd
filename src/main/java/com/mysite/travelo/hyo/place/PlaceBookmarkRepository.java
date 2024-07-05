package com.mysite.travelo.hyo.place;


import com.mysite.travelo.yeon.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Integer> {
    boolean existsByUserAndPlace(User user, Place place);
    void deleteByUserAndPlace(User user, Place place);
    List<PlaceBookmark> findByUser(User user);


}
