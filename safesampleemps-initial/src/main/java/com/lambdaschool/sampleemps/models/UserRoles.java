package com.lambdaschool.sampleemps.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "userroles")
@IdClass(UserRolesId.class)
public class UserRoles
		implements Serializable {

	@Id
	@OneToMany
	@NotNull
	@JoinColumn(name = "userid")
	@JsonIgnoreProperties(value = "roles",
	                      allowSetters = true)
	private User user;

	@Id
	@ManyToOne
	@NotNull
	@JoinColumn(name = "roleid")
	@JsonIgnoreProperties(value = "users",
	                      allowSetters = true)
	private Role role;

	public UserRoles() {}

	public UserRoles(
			@NotNull User user,
			@NotNull Role role
	) {
		this.user = user;
		this.role = role;
	}

	@Override
	public int hashCode() {
		return 34;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (!(o instanceof UserRoles)) { return false; }

		UserRoles that = (UserRoles) o;

		long thisUserId = (this.getUser() == null)
		                  ? 0L
		                  : this.getUser()
		                        .getUserid();
		long thatUserId = (that.getUser() == null)
		                  ? 0L
		                  : that.getUser()
		                        .getUserid();
		long thisRoleId = (this.getRole() == null)
		                  ? 0L
		                  : this.getRole()
		                        .getRoleid();
		long thatRoleId = (that.getRole() == null)
		                  ? 0L
		                  : that.getRole()
		                        .getRoleid();

		return (thisUserId == thatUserId) && (thisRoleId == thatRoleId);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
