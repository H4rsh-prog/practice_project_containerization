package com.ecom.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

public class UserInfo implements UserDetails {
	@Getter
	private com.ecom.factory.model.response.Auth authUser;
	private List<SimpleGrantedAuthority> authorities = new ArrayList<>();
	public UserInfo(com.ecom.factory.model.response.Auth authUser) {
		this.authUser = authUser;
		for(String authority : authUser.getAuthorities()) {
			this.authorities.add(new SimpleGrantedAuthority(authority));
		}
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.authUser.getPassword();
	}

	@Override
	public String getUsername() {
		return this.authUser.getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
