package com.lambdaschool.sampleemps.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
public class User extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userid;

	@NotNull
	@Column(unique = true,
	        nullable = false)
	private String username;

	@NotNull
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@OneToMany(mappedBy = "user",
	           cascade = CascadeType.ALL,
	           orphanRemoval = true)
	@JsonIgnoreProperties(value = "user",
	                      allowSetters = true)
	private Set<UserRoles> roles = new HashSet<>();

	public User() {}

	public User(
			@NotNull String username,
			@NotNull String password
	) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public User(
			@NotNull String username,
			@NotNull String password,
			Set<UserRoles> roles
	) {
		this.username = username;
		this.password = password;
		this.roles    = roles;
	}

	public void setPasswordNoEncrypt(String password) {
		this.password = password;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		this.setPasswordNoEncrypt(passwordEncoder.encode(password));
	}

	public Set<UserRoles> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}

	/**
	 * Internally, user security requires a list of authorities, roles, that the user has. This method is a simple way to provide those.
	 * Note that SimpleGrantedAuthority requests the format ROLE_role name all in capital letters!
	 *
	 * @return The list of authorities, roles, this user object has
	 */
	@JsonIgnore
	public List<SimpleGrantedAuthority> getAuthority() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (UserRoles userRoles : this.roles) {
			String myRole = "ROLE_" + userRoles.getRole()
			                                   .getName()
			                                   .toUpperCase();
			authorities.add(new SimpleGrantedAuthority(myRole));
		}
		return authorities;
	}

}
