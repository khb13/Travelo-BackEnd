package com.mysite.travelo.yeon.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<SiteUser, Integer> {

	// 로그인 한 username을 갖는 객체가 존재하는지 => 존재하면 true
	boolean existsByUsername(String username);
	
	// 로그인 username을 갖는 객체 반환
	Optional<SiteUser> findByUsername(String username);
	
	// 페이징 처리 된 회원 목록
	Page<SiteUser> findByRole(Pageable pageable, UserRole role);
	
	// 탈퇴 여부에 따른 회원 목록
	Page<SiteUser> findByRoleAndDelYn(Pageable pageable, UserRole role, String delYn);
	
	List<SiteUser> findByDelYn(String delYn);
	
}
