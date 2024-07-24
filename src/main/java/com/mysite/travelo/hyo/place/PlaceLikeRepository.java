package com.mysite.travelo.hyo.place;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.travelo.yeon.user.SiteUser;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Integer> {

//	사용자와 장소 순차번호로 좋아요를 찾음
	Optional<PlaceLike> findByPlaceAndAuthor(Place place, SiteUser author);
}
