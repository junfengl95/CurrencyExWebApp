package com.fdm.velocitytrade.security;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fdm.velocitytrade.model.User;

public class AuthUser implements org.springframework.security.core.userdetails.UserDetails {
	private User user;

	public AuthUser(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		System.out.println(new SimpleGrantedAuthority(this.user.getRole().toString()));
//		return Arrays.asList(new SimpleGrantedAuthority(this.user.getRole().toString()));
		return null;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
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
