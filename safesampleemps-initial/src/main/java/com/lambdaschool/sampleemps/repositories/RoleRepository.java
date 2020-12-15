package com.lambdaschool.sampleemps.repositories;


import com.lambdaschool.sampleemps.models.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository
		extends CrudRepository<Role, Long> {

	Role findByName(String rolename);

}
