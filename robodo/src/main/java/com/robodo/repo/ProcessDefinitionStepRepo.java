package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;

public interface ProcessDefinitionStepRepo extends CrudRepository<ProcessDefinitionStep, Long> {

}
