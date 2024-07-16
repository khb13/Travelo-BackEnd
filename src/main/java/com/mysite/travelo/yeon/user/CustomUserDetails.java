package com.mysite.travelo.yeon.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final SiteUser user;
    public CustomUserDetails(SiteUser user) {
        this.user = user;
    }

    // 현재 user의 role을 반환 (ex. "ROLE_ADMIN" / "ROLE_USER" 등)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return authorities;
    }

    // user의 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // user의 username 반환
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
	
}
