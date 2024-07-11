package com.mysite.travelo.hyo.place;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PlaceRepository extends JpaRepository<Place, Integer> {

	@Query("SELECT p FROM Place p ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Place> findAllOrderByLikeCountDescViewCountDesc(Pageable pageable);
	
}
