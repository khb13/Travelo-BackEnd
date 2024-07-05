package com.mysite.travelo.yeon.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<SiteUser, Integer> {

	// 로그인 한 username을 갖는 객체가 존재하는지 => 존재하면 true
	boolean existsByUsername(String username);
	
	// 로그인 username을 갖는 객체 반환
	Optional<SiteUser> findByUsername(String username);
	
}
