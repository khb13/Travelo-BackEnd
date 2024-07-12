package com.mysite.travelo.hyo.place;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Integer> {
    boolean existsByUserAndPlace(SiteUser user, Place place);
    void deleteByUserAndPlace(SiteUser user, Place place);
    List<PlaceBookmark> findByUser(SiteUser user);


}
