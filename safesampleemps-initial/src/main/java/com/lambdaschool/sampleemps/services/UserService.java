package com.lambdaschool.sampleemps.services;


import com.lambdaschool.sampleemps.models.User;


public interface UserService {

	User findByName(String name);
	User save(User user);

}
