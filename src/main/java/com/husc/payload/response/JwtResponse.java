package com.husc.payload.response;

import java.util.List;

public class JwtResponse {
	private String token;
	private String type ="Bearer";
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	
	public String getType() {
		return type;
	}
	public String getToken() {
		return token;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public JwtResponse(String token, Long id, String username, String email, List<String> roles) {
		this.token = token;
		this.id = id;
		this.username =  username;
		this.email = email;
		this.roles = roles;
	}
	
}
