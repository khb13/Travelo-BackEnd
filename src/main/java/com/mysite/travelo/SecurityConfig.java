package com.mysite.travelo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 2024.07.05
 * .csrf(csrf->csrf.disable()) 추가
 * 추가 사유
 * : 권한이 없다고 좋아요, 북마크 기능을 403으로 자꾸 반려함.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.
			authorizeHttpRequests(
					(authorizeHttpRequests) -> authorizeHttpRequests
							.requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
							.requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
		.headers(
				(headers) -> headers
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
						XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))).csrf(csrf -> csrf.disable()
			);
		
		return http.build();
	}


}
