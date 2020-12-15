package com.lambdaschool.sampleemps.services;


import com.lambdaschool.sampleemps.models.Role;
import com.lambdaschool.sampleemps.models.User;
import com.lambdaschool.sampleemps.models.UserRoles;
import com.lambdaschool.sampleemps.repositories.RoleRepository;
import com.lambdaschool.sampleemps.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Transactional
@Service(value = "userService")
public class UserServiceImpl
		implements UserService {

	private final UserRepository userRepo;
	private final RoleRepository roleRepo;

	@Autowired
	public UserServiceImpl(
			UserRepository userRepo,
			RoleRepository roleRepo
	) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}

	@Override
	public User findByName(String name)
			throws
			EntityNotFoundException {
		User user = userRepo.findByUsername(name.toLowerCase());
		if (user == null) {
			throw new EntityNotFoundException("User name " + name + " not found!");
		}
		return user;
	}

	@Transactional
	@Override
	public User save(User user)
			throws
			EntityNotFoundException {
		User newUser = new User();
		if (user.getUserid() != 0) {
			String errMessage = "User id " + user.getUserid() + " Not Found";
			userRepo.findById(user.getUserid())
			        .orElseThrow(() -> new EntityNotFoundException(errMessage));
			newUser.setUserid(user.getUserid());
		}
		newUser.setUsername(user.getUsername()
		                        .toLowerCase());
		newUser.setPasswordNoEncrypt(user.getPassword());
		newUser.getRoles()
		       .clear();
		for (UserRoles ur : user.getRoles()) {
			String errMessage = "Role id " + ur.getRole()
			                                   .getRoleid() + " Not Found";
			Role addRole = roleRepo.findById(ur.getRole()
			                                   .getRoleid())
			                       .orElseThrow(() -> new EntityNotFoundException(errMessage));
			newUser.getRoles()
			       .add(new UserRoles(
					       newUser,
					       addRole
			       ));
		}
		return userRepo.save(newUser);
	}

}
