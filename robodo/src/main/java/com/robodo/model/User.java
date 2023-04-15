package com.robodo.model;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;

import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User  {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	String username;
	String password;
	String fullname;
	String email;
	boolean valid;
	LocalDateTime lastLogin;
	LocalDateTime lastPasswordChange;
	LocalDateTime created;
	LocalDateTime updated;
	
	
	
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public LocalDateTime getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}
	public LocalDateTime getLastPasswordChange() {
		return lastPasswordChange;
	}
	public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
	}
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	public LocalDateTime getUpdated() {
		return updated;
	}
	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	@PrePersist
	protected void onCreate() {
		this.created = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated= LocalDateTime.now();
	}
	
	public UserDetails asUserDetails(ProcessService processService) {
		var userRoles = processService.getUserRoles(this);
		String[] roles=new String[userRoles.size()];
		for (int i=0;i<userRoles.size();i++) {
			roles[i]=userRoles.get(i).getRole();
		}
		UserDetails userDetails =
				org.springframework.security.core.userdetails.User.withUsername(this.username)
                        .password("{noop}%s".formatted(HelperUtil.decrypt(this.password)))
                        .roles(roles)
                        .disabled(!this.valid)
                        .build();
		
		return userDetails;
	}
	

	

}
