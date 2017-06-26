package com.earldouglas.dategrity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
public class User extends Tamperable {

	private long id;
	private String name;
	private String username;
	private String password;

	public User() {
	}

	public User(long id) {
		setId(id);
		setName("User " + +id);
		setPassword("password" + id);
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(name).append(username)
				.append(password).hashCode();
	}
}
