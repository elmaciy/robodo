package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;

public interface ProcessInstanceRepo extends CrudRepository<ProcessInstance, Long> {
	
	List<ProcessInstance> findByCode(String code);
	List<ProcessInstance>  findByProcessDefinition(ProcessDefinition processDefinition);
	List<ProcessInstance>  findByProcessDefinitionAndStatusAndRetryNoLessThan(ProcessDefinition processDefinition,String status, int retryNo);

}
