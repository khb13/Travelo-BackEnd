package com.mysite.travelo.hyo.place;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;
	
	public List<Place> findPopularPlaces() {
		Pageable pageable = PageRequest.of(0, 6);
		
		return placeRepository.findAllOrderByLikeCountDescViewCountDesc(pageable);
	}
	
}
