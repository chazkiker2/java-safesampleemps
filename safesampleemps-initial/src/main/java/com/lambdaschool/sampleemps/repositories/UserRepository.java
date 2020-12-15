package com.lambdaschool.sampleemps.repositories;


import com.lambdaschool.sampleemps.models.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository
		extends CrudRepository<User, Long> {

	User findByUsername(String username);

}
