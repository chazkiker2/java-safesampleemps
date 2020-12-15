package com.lambdaschool.sampleemps.controllers;


import com.lambdaschool.sampleemps.models.User;
import com.lambdaschool.sampleemps.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepo;

	@Autowired
	public UserController(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@GetMapping(value = "/getuserinfo",
	            produces = "application/json")
	public ResponseEntity<?> getCurrentUserInfo(Authentication authentication) {
		User u = userRepo.findByUsername(authentication.getName());
		return new ResponseEntity<>(
				u,
				HttpStatus.OK
		);
	}

	@GetMapping(value = "/getusername",
	            produces = "application/json")
	public ResponseEntity<?> getCurrentUserName(Authentication authentication) {
		return new ResponseEntity<>(
				authentication.getPrincipal(),
				HttpStatus.OK
		);
	}

}
