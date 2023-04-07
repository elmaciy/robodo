package com.robodo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.robodo.model.EmailTemplate;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.repo.EmailTemplateRepo;
import com.robodo.repo.ProcessDefinitionRepo;
import com.robodo.repo.ProcessInstanceRepo;
import com.robodo.singleton.SingletonForUIUpdate;

@Service
public class ProcessService {
	
	@Autowired
	Environment env;
	
	@Autowired
	ProcessDefinitionRepo processDefinitionRepo;
	
	
	@Autowired
	ProcessInstanceRepo processInstanceRepo;
	
	@Autowired
	EmailTemplateRepo emailTemplateRepo;
	
	public List<ProcessDefinition> getProcessDefinitions() {
		return StreamSupport.stream(processDefinitionRepo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	public void saveProcessInstance(ProcessInstance processInstance) {
		processInstanceRepo.save(processInstance);
		SingletonForUIUpdate.getInstance().setLastUpdate();
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
	
	public ProcessInstance getProcessInstanceByCode(String code) {
		List<ProcessInstance> list = processInstanceRepo.findByCode(code);
		if (list.size()==0) return null;
		return list.get(0);
		
	}

	public List<ProcessInstance> getProcessNotCompletedInstances(ProcessDefinition processDefinition, int maxInstance) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();

		if (!processDefinition.isActive()) return instances;
		
		List<ProcessInstance> newInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndAttemptNoLessThan(processDefinition, ProcessInstance.STATUS_NEW, processDefinition.getMaxAttemptCount());
		for (ProcessInstance instance : newInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;
		
		List<ProcessInstance> retryInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndAttemptNoLessThan(processDefinition, ProcessInstance.STATUS_RETRY, processDefinition.getMaxAttemptCount());
		for (ProcessInstance instance : retryInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;

		List<ProcessInstance> runningInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndAttemptNoLessThan(processDefinition, ProcessInstance.STATUS_RUNNING, processDefinition.getMaxAttemptCount());
		
		for (ProcessInstance instance : runningInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
	
		return instances;
	}
	
	public List<ProcessInstance> getProcessFailedAndToBeRetriedInstances(ProcessDefinition processDefinition, int maxInstance) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();

		if (!processDefinition.isActive()) return instances;
		
		List<ProcessInstance> newInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndAttemptNoLessThanAndFailed(processDefinition, ProcessInstance.STATUS_COMPLETED, processDefinition.getMaxAttemptCount(),true);
		for (ProcessInstance instance : newInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;
		
		List<ProcessInstance> runningInstances = processInstanceRepo.findByProcessDefinitionAndStatusAndAttemptNoLessThanAndFailed(processDefinition, ProcessInstance.STATUS_COMPLETED, processDefinition.getMaxAttemptCount(), true);
		
		for (ProcessInstance instance : runningInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
	
		return instances;
	}

	public Environment getEnv() {
		return env;
	}

	public List<ProcessInstance> getProcessInstancesByProcessDefinition(ProcessDefinition processDefinition) {
		return processInstanceRepo.findByProcessDefinition(processDefinition);
	}
	
	public EmailTemplate getEmailTemplateByCode(String code) {
		List<EmailTemplate> list = emailTemplateRepo.findByCode(code);
		if (list.size()==0) {
			return null;
		}
		return list.get(0);
	}

	

}
