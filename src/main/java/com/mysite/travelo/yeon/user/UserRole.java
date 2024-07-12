package com.mysite.travelo.yeon.user;

import lombok.Getter;

@Getter
public enum UserRole {

	ADMIN, USER;
	
	public String getRole() {
        return this.name();
    }
	
}
