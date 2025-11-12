package com.portfolio.Utility.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.portfolio.DAO.UserDAO;
import com.portfolio.Entity.UserEntity;
import com.portfolio.Utility.CommonUtils;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserDAO userDAO;

	public CustomUserDetailsService(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CommonUtils.logMethodEntry(this);
		UserEntity user = userDAO.findTopByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return new CustomUserDetails(user);
	}

}
