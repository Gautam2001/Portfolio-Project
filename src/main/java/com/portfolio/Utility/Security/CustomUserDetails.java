package com.portfolio.Utility.Security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.portfolio.Entity.UserEntity;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final UserEntity userEntity;

	public CustomUserDetails(UserEntity userEntity) {
		super();
		this.userEntity = userEntity;
	}

	@Override
	public String getUsername() {
		return userEntity.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

}
