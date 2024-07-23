package com.mysite.travelo.hyo.place;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mysite.travelo.gil.course.Course;
import com.mysite.travelo.gil.course.CourseLike;
import com.mysite.travelo.yeon.user.SiteUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* *
 * 장소 서비스
 * 1. List<Place> findAll() : 리스트의 형태로 전체를 가져옴
 *
 * 2. Page<Place> getPage(int page, String keyword, String sort, List<String> content_list, List<String> area_list)
 * : 페이지를 가져옴
 * : 입력 받을 내용: 페이지 당 아이템 수, 페이지 번호, 키워드, 정렬, 콘텐츠 코드, 지역 코드
 * : url 형식 : /place/list?item=&page=&keyword=&sorts=&content=&area=
 *
 * 3. Page<Place> getPage(int page, String keyword, double latitude, double longitude, double distance, List<String> content_list)
 * : 특정 컨텐츠 ID를 기준으로 입력 받은 반경 거리 내에서 키워드, 장소 유형에 따라 가까운 순으로 정렬하는 기능
 * : 입력 받을 내용: 페이지 당 아이템 수, 페이지 번호, 키워드, 콘텐츠 ID(콘텐츠 ID를 통해서 latitude, longitude 추출), 반경 거리, 콘텐츠 코드
 * : url 형식 : /place/distance?item=&page=&keyword=&contentId=&distance=&content=
 *
 * 4-1. increaseLike(int placeSeq)
 * : 좋아요 증가 기능
 * : 입력 받을 내용: 장소 순차번호 (콘텐츠 ID 아님. 주의)
 *
 * 4-2. decreaseLike(int placeSeq)
 * : 좋아요 해제 기능
 * : 입력 받을 내용: 장소 순차번호 (콘텐츠 ID 아님. 주의)
 *
 * 5. increaseViewCount(int placeSeq)
 * : 조회수 증가 기능
 * : 입력 받을 내용: 장소 순차번호 (콘텐츠 ID 아님. 주의)
 *
 * Specification<Place> search(keyword) : 검색하는 기능
 * Specification<Place> content : 카테고리 - 장소 유형
 * Specification<Place> area : 카테고리 - 지역
 * Specification<Place> distance : 반경 계산 기능
 */

@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceLikeRepository placeLikeRepository;

    public List<Place> findPopularPlaces() {
        Pageable pageable = PageRequest.of(0, 6);

        return placeRepository.findAllOrderByLikeCountDescViewCountDesc(pageable);
    }

    // 리스트의 형태로 전체를 가져옴
    public List<Place> findAll() {
        return this.placeRepository.findAll();
    }

    // contentId로 Place 객체 찾기
    public Place getPlaceByContentId(String contentId) {
        return placeRepository.findByContentId(contentId);
    }

    // 기본 리스트
    public Page<Place> getPage(int item_in_page, int page, String keyword, String sort, List<String> content_list, List<String> area_list) {

        // 정렬
        List<Sort.Order> sorts = new ArrayList<>();

        System.out.println("sorts2" + sort);

        // 인기순과 아님으로 나누어짐.
        if (sort.equals("popular")) {
            sorts.add(Sort.Order.desc("likeCount"));
            sorts.add(Sort.Order.desc("viewCount"));
        } else {
            sorts.add(Sort.Order.desc("placeSeq"));
        }

        // 페이지
        Pageable pageable = PageRequest.of(page, item_in_page, Sort.by(sorts));

     // 검색, 카테고리(장소 유형, 지역)) : 장소 유형, 지역 모두 코드 번호로 받아옴.
        Specification<Place> spec = Specification.where(area(area_list))
                                        .and(content(content_list))
                                        .and(search(keyword));

        // 리턴
        return this.placeRepository.findAll(spec, pageable);
    }

    // 거리 검색 리스트
    public Page<Place> getPage(int item_in_page, int page, String keyword, double latitude, double longitude, double distance, List<String> content_list) {

        Pageable pageable = PageRequest.of(page, item_in_page);

        Specification<Place> spec = Specification.where((content(content_list))
                                                .and(search(keyword).and(distance(latitude, longitude, distance))));

        return this.placeRepository.findAll(spec, pageable);
    }

    // 거리 검색 리스트
    public List<Place> getPage(String keyword, double latitude, double longitude, double distance, List<String> content_list) {

        Specification<Place> spec = Specification.where((content(content_list))
                .and(search(keyword).and(distance(latitude, longitude, distance))));

        return this.placeRepository.findAll(spec);
    }

//	좋아요 상태관리
    public void togglePlaceLike(Integer placeSeq, SiteUser user) {
        // Place 엔티티를 가져옴
        Optional<Place> op = placeRepository.findByPlaceSeq(placeSeq);
        Place place = op.get();

        // 이미 좋아요가 존재하는지 확인
        Optional<PlaceLike> opl = placeLikeRepository.findByPlaceAndAuthor(place, user);
        
        if (opl.isPresent()) {
            PlaceLike placeLike = opl.get();
            if ("Y".equals(placeLike.getLikeYn())) {
                // 현재 좋아요 상태면, 좋아요 취소로 변경 및 좋아요 갯수 감소
                placeLike.setLikeYn("N");
                decreaseLike(placeSeq);
            } else {
                // 현재 좋아요 취소 상태면, 좋아요로 변경 및 좋아요 갯수 증가
                placeLike.setLikeYn("Y");
                increaseLike(placeSeq);
            }
            placeLikeRepository.save(placeLike);
        } else {
            // 좋아요가 존재하지 않을 경우 새로 추가
            PlaceLike placeLike = new PlaceLike();
            placeLike.setPlace(place);
            placeLike.setAuthor(user);
            placeLike.setLikeYn("Y");
            placeLikeRepository.save(placeLike);
            increaseLike(placeSeq);
        }
    }

    // 좋아요 증가 감소
    @Transactional
    public int increaseLike(int placeSeq) {
        Place place = placeRepository.findById(placeSeq);
        place.setLikeCount(place.getLikeCount() + 1);
        placeRepository.save(place);

        return place.getLikeCount();
    }
    @Transactional
    public int decreaseLike(int placeSeq) {
        Place place = placeRepository.findById(placeSeq);
        place.setLikeCount(place.getLikeCount() - 1);
        placeRepository.save(place);

        return place.getLikeCount();
    }


    // 조회수 기능
    @Transactional
    public Place increaseViewCount(int placeSeq) {
        Place place = placeRepository.findById(placeSeq);
        place.setViewCount(place.getViewCount() + 1);
        return placeRepository.save(place);
    }

    // 검색 기능
    private Specification<Place> search(String keyword){
        return (Root<Place> place, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                query.distinct(true);
                if(keyword == null || keyword.isEmpty()){
                    return cb.conjunction();
                }

                return cb.like(place.get("title"), '%' + keyword + '%');
        };
    }

    // 카테고리 기능 - 지역 유형
    private Specification<Place> content(List<String> content_list) {
        return (Root<Place> place, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            if(content_list == null || content_list.isEmpty()){
                return cb.conjunction();
            }

            Predicate[] predicates = content_list.stream()
                .map(content -> cb.equal(place.get("type"), content))
                .toArray(Predicate[]::new);

            return cb.or(predicates);
        };
    }

    // 카테고리 기능 - 지역
    private Specification<Place> area(List<String> area_list) {
        return (Root<Place> place, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            if(area_list == null || area_list.isEmpty()){
                return cb.conjunction();
            }

            Predicate[] predicates = area_list.stream()
                .map(area -> cb.equal(place.get("areaCode"), area))
                .toArray(Predicate[]::new);

            return cb.or(predicates);
        };
    }

    // 반경 검색
    private Specification<Place> distance(double latitude, double longitude,  double distance){
        return (Root<Place> place, CriteriaQuery<?> query, CriteriaBuilder cb)->{
            query.distinct(true);

            //1km 당 경도, 위도 계산
            double longitudeDistance = distance / 88; //x, long
            double latitudeDistance = distance / 111; //y, lati

            // 최소 x,y 좌표와 최대 x,y 좌표를 구하는 식
            // radius는 반경 몇 km 이내로 할 것인지를
            double min_x = longitude - longitudeDistance;
            double min_y = latitude - latitudeDistance;
            double max_x = longitude + longitudeDistance;
            double max_y = latitude + latitudeDistance;

            // 위도와 경도 간의 거리 계산 및 절대값 비교
            Predicate longitudePredicate = cb.between(place.get("longitude"), min_x, max_x);
            Predicate latitudePredicate = cb.between(place.get("latitude"), min_y, max_y);

            Expression<Double> absDiffLatitude = cb.abs(cb.diff(place.get("latitude"), latitude));
            Expression<Double> absDiffLongitude = cb.abs(cb.diff(place.get("longitude"), longitude));

            Order distanceOrder = cb.asc(cb.sum(absDiffLatitude, absDiffLongitude));
            query.orderBy(distanceOrder);

            return cb.and(longitudePredicate, latitudePredicate);
        };
    }



}
