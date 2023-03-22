package com.robodo.repo;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessDefinition;

public interface ProcessDefinitionRepo extends CrudRepository<ProcessDefinition, Long> {

}
