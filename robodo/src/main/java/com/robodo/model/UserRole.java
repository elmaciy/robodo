package com.robodo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRole {
	
	public static final String ROLE_ADMIN="ADMIN";
	public static final String ROLE_USER="USER";
	public static final String SUPERVISOR = "SUPERVISOR";
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	Long userId;
	String role;
	
	
	LocalDateTime created;
	
	
	public UserRole() {
	}
	
	
	
	public UserRole(Long userId, String role) {
		this.userId=userId;
		this.role=role;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@PrePersist
	protected void onCreate() {
		this.created = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return role;
	}
	
	
}
