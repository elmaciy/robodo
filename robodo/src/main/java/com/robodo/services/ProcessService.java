package com.robodo.services;

import java.util.ArrayList;
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

	public boolean saveProcessDefinition(ProcessDefinition p) {
		try {
			processDefinitionRepo.save(p);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean isProcessInstanceAlreadyExists(ProcessInstance discoveredInstance) {
		List<ProcessInstance> list = processInstanceRepo.findByCode(discoveredInstance.getCode());
		return !list.isEmpty();
	}

	public List<ProcessInstance> getProcessNotCompletedInstances(int maxInstance) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
		List<ProcessDefinition> processDefinitions = getProcessDefinitions();
		for (ProcessDefinition processDefinition : processDefinitions) {
			if (!processDefinition.isActive()) continue;
			
			List<ProcessInstance> newInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndRetryNoLessThan(processDefinition, ProcessInstance.STATUS_NEW, processDefinition.getMaxRetryCount());
			for (ProcessInstance instance : newInstances) {
				if (instances.size()>=maxInstance) break;
				instances.add(instance);
			}
			
			if (instances.size()>=maxInstance) break;
			
			List<ProcessInstance> runningInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndRetryNoLessThan(processDefinition, ProcessInstance.STATUS_RUNNING, processDefinition.getMaxRetryCount());
			
			for (ProcessInstance instance : runningInstances) {
				if (instances.size()>=maxInstance) break;
				instances.add(instance);
			}
			
		}
		return instances;
	}

}
