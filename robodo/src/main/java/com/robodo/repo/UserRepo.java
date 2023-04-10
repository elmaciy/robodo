package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.User;

public interface UserRepo extends CrudRepository<User, Long> {
	List<User> findByUsernameAndPassword(String username, String password);
	List<User>  findByUsername(String username);
	List<User> findByValid(boolean valid);

}
