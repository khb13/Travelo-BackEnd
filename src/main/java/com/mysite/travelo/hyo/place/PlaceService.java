package com.mysite.travelo.hyo.place;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/* *
 * 장소 서비스
 * List<Place> findAll() : 리스트의 형태로 전체를 가져옴
 * Page<Place> getPage(int page, String keyword, String sort, List<String> content_list, List<String> area_list)
 * : 페이지를 가져옴
 * : 입력 받을 내용: 페이지 번호, 키워드, 정렬, 콘텐츠 코드, 지역 코드
 * : url 형식 : /place/list?page=&keyword=&sorts=&content=&area=
 *
 * Specification<Place> search(keyword) : 검색하는 기능
 * Specification<Place> content : 카테고리 - 장소 유형
 * Specification<Place> area : 카테고리 - 지역
 */

@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    // 리스트의 형태로 전체를 가져옴
    public List<Place> findAll() {
        return this.placeRepository.findAll();
    }

    // 페이지를 가져옴
    public Page<Place> getPage(int page, String keyword, String sort, List<String> content_list, List<String> area_list) {

        // 정렬
        List<Sort.Order> sorts = new ArrayList<>();

        // 인기순과 아님으로 나누어짐.
        if (sort.equals("popular")) {
            sorts.add(Sort.Order.desc("pLikeCount"));
            sorts.add(Sort.Order.desc("pViewCount"));
        } else {
            sorts.add(Sort.Order.desc("pSeq"));
        }

        // 페이지
        int item_in_page = 15;
        Pageable pageable = PageRequest.of(page, item_in_page, Sort.by(sorts));

        // 검색, 카테고리(장소 유형, 지역)) : 장소 유형, 지역 모두 코드 번호로 받아옴.
        Specification<Place> spec = Specification.where(search(keyword)).and(content(content_list)).and(area(area_list));

        // 리턴
        return this.placeRepository.findAll(spec, pageable);
    }

    // 검색 기능
    private Specification<Place> search(String keyword){
        return (Root<Place> place, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                query.distinct(true);
                if(keyword == null || keyword.isEmpty()){
                    return cb.conjunction();
                }

                return cb.like(place.get("pTitle"), '%' + keyword + '%');
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
                .map(area -> cb.equal(place.get("pAreaCode"), area))
                .toArray(Predicate[]::new);

            return cb.or(predicates);
        };
    }
}
