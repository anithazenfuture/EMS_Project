package com.example.EMS.EmployeeDTO;

import java.util.Set;

import com.example.EMS.enums.Role;

import lombok.Data;


@Data
public class LoginResponse {
    private String email;
    private String name;
    private String token;
    private Set<Role> role;
    
    
    
    public Set<Role> getRole() {
		return role;
	}
	public void setRole(Set<Role> role) {
		this.role = role;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
}

