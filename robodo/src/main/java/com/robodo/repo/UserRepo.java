package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.User;

public interface UserRepo extends CrudRepository<User, Long> {
	List<User>  findByUsername(String username);

}
