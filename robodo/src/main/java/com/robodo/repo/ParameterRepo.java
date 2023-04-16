package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.CorporateParameter;

public interface ParameterRepo extends CrudRepository<CorporateParameter, Long> {

	List<CorporateParameter> findAllByCode(String code);
	

}
