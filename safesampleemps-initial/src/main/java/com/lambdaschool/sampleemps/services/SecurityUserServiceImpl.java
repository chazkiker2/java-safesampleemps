package com.lambdaschool.sampleemps.services;


import com.lambdaschool.sampleemps.models.User;
import com.lambdaschool.sampleemps.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Service(value = "securityUserService")
public class SecurityUserServiceImpl
		implements UserDetailsService {

	private final UserRepository userRepo;

	@Autowired
	public SecurityUserServiceImpl(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	/**
	 * Verifies that the user is correct and if so creates the authenticated user
	 *
	 * @param username The user name we are look for
	 * @return a security user detail that is now an authenticated user
	 * @throws EntityNotFoundException if the user name is not found
	 */
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username)
			throws
			EntityNotFoundException {
		User user = userRepo.findByUsername(username.toLowerCase());
		if (user == null) {
			throw new EntityNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.getAuthority()
		);

	}

}
