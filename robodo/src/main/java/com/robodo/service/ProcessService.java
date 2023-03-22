package com.robodo.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.repo.ProcessDefinitionRepo;
import com.robodo.repo.ProcessInstanceRepo;

@Service
public class ProcessService {
	
	@Autowired
	ProcessDefinitionRepo processDefinitionRepo;
	
	
	@Autowired
	ProcessInstanceRepo processInstanceRepo;
	
	public List<ProcessDefinition> getProcessDefinitions() {
		return StreamSupport.stream(processDefinitionRepo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	public void saveProcessInstance(ProcessInstance processInstance) {
		processInstanceRepo.save(processInstance);
		
	}

}
