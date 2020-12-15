package com.lambdaschool.sampleemps.services;


import com.lambdaschool.sampleemps.models.Role;


public interface RoleService {
	Role findByName(String name);
}
