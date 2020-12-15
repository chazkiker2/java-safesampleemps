package com.lambdaschool.sampleemps.services;


import com.lambdaschool.sampleemps.models.Role;
import com.lambdaschool.sampleemps.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Transactional
@Service(value = "roleService")
public class RoleServiceImpl
		implements RoleService {

	private final RoleRepository roleRepo;

	@Autowired
	public RoleServiceImpl(RoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}

	@Override
	public Role findByName(String name)
			throws
			EntityNotFoundException {
		Role rr = roleRepo.findByName(name);
		if (rr != null) {
			return rr;
		} else {
			throw new EntityNotFoundException(name);
		}
	}

}
