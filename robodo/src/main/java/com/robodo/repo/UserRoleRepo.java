package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.UserRole;

public interface UserRoleRepo extends CrudRepository<UserRole, Long> {
	List<UserRole> findByUserId(Long userId);
	

}
