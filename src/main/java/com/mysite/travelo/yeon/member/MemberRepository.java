package com.mysite.travelo.yeon.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

	Optional<Member> findByusername(String username);
	Optional<Member> findBynickname(String nickname);
	
}
