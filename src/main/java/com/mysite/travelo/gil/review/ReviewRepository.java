package com.mysite.travelo.gil.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

//	특정 코스의 공개 댓글 전체보기(정렬 디폴트값: 추천순 / 옵션값: 최신순, 오래된순)
	Page<Review> findByCourseCourseSeqAndBlindYn(Pageable pageable, Integer courseSeq, String blindYn);
	
//	특정 코스의 공개 댓글 갯수 조회
    int countByCourseCourseSeqAndBlindYn(Integer courseSeq, String blindYn);
	
//	특정 유저의 댓글 전체보기(정렬 디폴트값: 최신순 / 옵션값: 오래된순)
	Page<Review> findByUserUserSeqAndBlindYn(Pageable pageable, Integer userSeq, String blindYn);
	
//	특정 유저의 비공개 댓글 갯수 조회
	int countByUserUserSeqAndBlindYn(Integer userSeq, String blindYn);
	
//	신고 수 5회 이상인 공개 댓글 전체보기(정렬 디폴트값: 오래된순)
	Page<Review> findByReportCountGreaterThanEqualAndBlindYn(Pageable pageable, Integer reportCount, String blindYn);
	
}
