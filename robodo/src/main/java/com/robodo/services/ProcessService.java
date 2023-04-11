package com.robodo.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.robodo.model.EmailTemplate;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.Tokenization;
import com.robodo.model.User;
import com.robodo.repo.EmailTemplateRepo;
import com.robodo.repo.ProcessDefinitionRepo;
import com.robodo.repo.ProcessInstanceRepo;
import com.robodo.repo.ProcessInstanceStepFileRepo;
import com.robodo.repo.TokenizationRepo;
import com.robodo.repo.UserRepo;
import com.robodo.utils.HelperUtil;

@Service
@Transactional
public class ProcessService {
	
	@Autowired
	Environment env;
	
	@Autowired
	ProcessDefinitionRepo processDefinitionRepo;
	
	
	@Autowired
	ProcessInstanceRepo processInstanceRepo;
	

	@Autowired
	ProcessInstanceStepFileRepo processInstanceStepFileRepo;
	
	@Autowired
	TokenizationRepo tokenizationRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	EmailTemplateRepo emailTemplateRepo;
	
	public List<ProcessDefinition> getProcessDefinitions() {
		return StreamSupport.stream(processDefinitionRepo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	public void saveProcessInstance(ProcessInstance processInstance) {		
		processInstanceRepo.save(processInstance);
	}
	
	public void updateQueueDate(ProcessInstance processInstance) {
		processInstance.setQueued(LocalDateTime.now());
		processInstanceRepo.save(processInstance);
	}

	public boolean saveProcessDefinition(ProcessDefinition p) {
		try {
			processDefinitionRepo.save(p);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
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

	public List<ProcessInstance> getNotCompletedInstances(ProcessDefinition processDefinition, int maxInstance) {
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
	
	public List<ProcessInstance> getProcessInstancesByProcessDefinitionAndStatus(ProcessDefinition processDefinition, String status) {
		return processInstanceRepo.findByProcessDefinitionAndStatus(processDefinition, status);
	}
	
	public EmailTemplate getEmailTemplateByCode(String code) {
		List<EmailTemplate> list = emailTemplateRepo.findByCode(code);
		if (list.size()==0) {
			return null;
		}
		return list.get(0);
	}

	public List<ProcessInstanceStepFile> getProcessInstanceStepFilesByStepId(ProcessInstanceStep step) {
		var list = processInstanceStepFileRepo.findByProcessInstanceStepId(step.getId());
		Collections.sort(list, new Comparator<ProcessInstanceStepFile>() {

			@Override
			public int compare(ProcessInstanceStepFile o1, ProcessInstanceStepFile o2) {
				return Integer.compare(o1.getFileOrder(), o2.getFileOrder());
			}
		});
		
		return list;
	}

	public void saveProcessInstanceStepFile(ProcessInstanceStepFile file) {
		processInstanceStepFileRepo.save(file);
		
	}

	public void deleteAllStepFiles(ProcessInstanceStep step) {
		processInstanceStepFileRepo.deleteByProcessInstanceStepId(step.getId());
		
	}

	public void saveToken(Tokenization token) {
		tokenizationRepo.save(token);
		
	}

	public boolean isValidToken(String token, String purpose, String purposeDetail) {
		List<Tokenization> list = tokenizationRepo.findByTokenAndPurposeAndPurposeDetail(token, purpose, purposeDetail);
		if (list.isEmpty()) return false;
		
		Tokenization tokenization = list.get(0);
		var now = LocalDateTime.now();
		return tokenization.getValidFrom().isBefore(now) && tokenization.getValidTo().isAfter(now);
		
	}

	public Tokenization getToken(String purpose, String purposeDetail) {
		List<Tokenization> list = tokenizationRepo.findByPurposeAndPurposeDetail( purpose, purposeDetail);
		if (list.isEmpty()) return null;
		return list.get(0);
	}

	public List<Tokenization> getTokensToRemove() {
		return tokenizationRepo.findByValidToBefore(LocalDateTime.now());
	}

	public void removeToken(Tokenization token) {
		tokenizationRepo.delete(token);
	}

	public User getUserByUsernameAndPassword(String username, String password) {
		String passwordEncoded = HelperUtil.encrypt(password);
		List<User> userList = userRepo.findByUsernameAndPassword(username, passwordEncoded);
		if (userList.isEmpty()) return null;
		return userList.get(0);
	}
	
	public User getUserByUsername(String username) {
		List<User> userList = userRepo.findByUsername(username);
		if (userList.isEmpty()) return null;
		return userList.get(0);
	}

	public List<User> getActiveUsers() {
		return userRepo.findByValid(true);		
	}

	

}
