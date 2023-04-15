package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.Parameter;

public interface ParameterRepo extends CrudRepository<Parameter, Long> {

	List<Parameter> findAllByCode(String code);
	

}
