package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;

public interface ProcessInstanceRepo extends CrudRepository<ProcessInstance, Long> {
	
	List<ProcessInstance> findByCode(String code);
	List<ProcessInstance>  findByProcessDefinition(ProcessDefinition processDefinition);
	List<ProcessInstance>  findByProcessDefinitionAndStatus(ProcessDefinition processDefinition, String status);
	List<ProcessInstance>  findByProcessDefinitionAndStatusAndAttemptNoLessThan(ProcessDefinition processDefinition,String status, int attemptNo);
	List<ProcessInstance>  findByProcessDefinitionAndStatusAndAttemptNoLessThanAndFailed(ProcessDefinition processDefinition,String status, int attemptNo, boolean failed);
	
	

}
