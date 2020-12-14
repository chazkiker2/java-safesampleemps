package com.lambdaschool.sampleemps.models;


/**
 * A model used to create a new user. The minimum information needed to create a user. Note that the role will
 * default to USER.
 */
public class UserMinimum {

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
