package com.robodo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRole {
	
	public static final String ROLE_ADMIN="ADMIN";
	public static final String ROLE_USER="USER";
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	User user;
	String role;
	
	
	LocalDateTime created;
	
	
	public UserRole() {
	}
	
	public UserRole(User user, String role) {
		this.user=user;
		this.role=role;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
