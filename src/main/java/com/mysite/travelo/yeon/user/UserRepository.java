package com.mysite.travelo.yeon.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

	// 로그인 한 username을 갖는 객체가 존재하는지 => 존재하면 true
	   boolean existsByUsername(String username);
	   
	// 로그인 username을 갖는 객체 반환
	Optional<User> findByUsername(String username);

}
