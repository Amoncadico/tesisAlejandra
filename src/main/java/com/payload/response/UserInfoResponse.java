package com.payload.response;

import java.util.List;

public class UserInfoResponse {
	private Long id;
	private String username;
	private List<String> roles;
	private String token;

	public UserInfoResponse(Long id, String username, List<String> roles) {
		this.id = id;
		this.username = username;
		this.roles = roles;
	}

	public UserInfoResponse(Long id, String username, List<String> roles, String token) {
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getToken() {
		return token;
	}
}
