package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessDefinition;

public interface ProcessDefinitionRepo extends CrudRepository<ProcessDefinition, Long> {
	List<ProcessDefinition> findByCode(String code);

}
