package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessInstance;

public interface ProcessInstanceRepo extends CrudRepository<ProcessInstance, Long> {
	
	List<ProcessInstance> findByCode(String code);
	List<ProcessInstance>  findByProcessDefinitionId(Long processDefinitionId);
	List<ProcessInstance>  findTop1ByProcessDefinitionId(Long processDefinitionId);
	List<ProcessInstance>  findByProcessDefinitionIdAndStatus(Long processDefinitionId, String status);
	List<ProcessInstance>  findByProcessDefinitionIdAndStatusAndAttemptNoLessThan(Long processDefinitionId,String status, int attemptNo);
	List<ProcessInstance>  findByProcessDefinitionIdAndStatusAndAttemptNoLessThanAndFailed(Long processDefinitionId,String status, int attemptNo, boolean failed);
	

}
