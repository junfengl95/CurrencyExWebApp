package com.fdm.velocitytrade.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.repo.UserRepository;

@Service
public class AuthUserService implements org.springframework.security.core.userdetails.UserDetailsService {
	private UserRepository userRepo;

	@Autowired
	public AuthUserService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	}

	@Override
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = this.userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return new AuthUser(user);
	}

}
