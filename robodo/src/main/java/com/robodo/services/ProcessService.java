package com.robodo.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.robodo.model.EmailTemplate;
import com.robodo.model.Parameter;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.Tokenization;
import com.robodo.model.User;
import com.robodo.model.UserRole;
import com.robodo.repo.EmailTemplateRepo;
import com.robodo.repo.ParameterRepo;
import com.robodo.repo.ProcessDefinitionRepo;
import com.robodo.repo.ProcessInstanceRepo;
import com.robodo.repo.ProcessInstanceStepFileRepo;
import com.robodo.repo.TokenizationRepo;
import com.robodo.repo.UserRepo;
import com.robodo.repo.UserRoleRepo;
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
	UserRoleRepo userRoleRepo;
	
	@Autowired
	EmailTemplateRepo emailTemplateRepo;
	
	@Autowired
	ParameterRepo parameterRepo;
	
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
		
		List<ProcessInstance> newInstances = processInstanceRepo.findByProcessDefinitionIdAndStatusAndAttemptNoLessThan(processDefinition.getId(), ProcessInstance.STATUS_NEW, processDefinition.getMaxAttemptCount());
		for (ProcessInstance instance : newInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;
		
		List<ProcessInstance> retryInstances = processInstanceRepo.findByProcessDefinitionIdAndStatusAndAttemptNoLessThan(processDefinition.getId(), ProcessInstance.STATUS_RETRY, processDefinition.getMaxAttemptCount());
		for (ProcessInstance instance : retryInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;

		List<ProcessInstance> runningInstances = processInstanceRepo.findByProcessDefinitionIdAndStatusAndAttemptNoLessThan(processDefinition.getId(), ProcessInstance.STATUS_RUNNING, processDefinition.getMaxAttemptCount());
		
		for (ProcessInstance instance : runningInstances) {
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
	
		return instances;
	}
	
	public List<ProcessInstance> getProcessFailedAndToBeRetriedInstances(ProcessDefinition processDefinition, int maxInstance) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();

		if (!processDefinition.isActive()) return instances;
		
		List<ProcessInstance> newInstances = processInstanceRepo.findByProcessDefinitionIdAndStatusAndAttemptNoLessThanAndFailed(processDefinition.getId(), ProcessInstance.STATUS_COMPLETED, processDefinition.getMaxAttemptCount(),true);
		for (ProcessInstance instance : newInstances) {

			ProcessInstanceStep currentStep=instance.getCurrentStep();
						
			if (currentStep!=null &&  currentStep.isHumanInteractionStep() && currentStep.isNotificationSent() && !currentStep.isApproved()) {
				continue;
			}
			
			if (instances.size()>=maxInstance) break;
			instances.add(instance);
		}
		
		if (instances.size()>=maxInstance) return instances;

		return instances;
	}

	public Environment getEnv() {
		return env;
	}

	public List<ProcessInstance> getProcessInstancesByProcessDefinition(ProcessDefinition processDefinition) {
		return processInstanceRepo.findByProcessDefinitionId(processDefinition.getId());
	}
	
	public List<ProcessInstance> getProcessInstancesByProcessDefinitionAndStatusAndSearchString(
			ProcessDefinition processDefinition, String status, 
			String searchString, boolean anyMatches) {
		var results= processInstanceRepo.findByProcessDefinitionIdAndStatus(processDefinition.getId(), status);
		return results.stream()
				.filter(p-> {
					String str="%s %s %s".formatted(p.getCode(),p.getDescription(),p.getInstanceVariables());
					return containsString(str,searchString,anyMatches);
				})
				.collect(Collectors.toList());
	}
	
	private boolean containsString(String string, String searchString, boolean anyMatches) {
		String clearedSearchString=searchString.replaceAll(" |\t", " ").strip();
		if (clearedSearchString.isBlank()) {
			return true;
		}
		
		List<String> keywords = Splitter.on(" ").omitEmptyStrings().splitToList(clearedSearchString);

		if (anyMatches) {
			return keywords.stream().anyMatch(kw->StringUtils.containsIgnoreCase(string,kw));
		}
		
		return keywords.stream().allMatch(kw->StringUtils.containsIgnoreCase(string,kw));

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

	public void deleteAllFiles(ProcessInstance instance) {
		instance.getSteps().forEach(step->{
			processInstanceStepFileRepo.deleteByProcessInstanceStepId(step.getId());	
		});
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

	public ProcessDefinition getProcessDefinitionById(Long processDefinitionId) {
		return getProcessDefinitions().stream().filter(p->p.getId().equals(processDefinitionId)).findFirst().get();
	}

	public void deleteProcessInstance(ProcessInstance processInstance) {
		processInstanceRepo.delete(processInstance);
	}

	public List<User> getUsersAll() {
		return Lists.newArrayList(userRepo.findAll());
	}

	public User saveUser(User user) {
		return userRepo.save(user);
		
	}

	public void removeUser(User user) {
		userRepo.delete(user);
		
	}

	public List<String> getRoles() {
		return List.of("ADMIN","USER","SUPERVISOR");
	}

	public List<UserRole> getUserRoles(User user) {
		return userRoleRepo.findByUserId(user.getId());
	}

	public void removeRolesByUser(User user) {
		getUserRoles(user).stream().forEach(userRole->{
			userRoleRepo.delete(userRole);
		});
		
	}

	public void saveUserRole(UserRole userRole) {
		userRoleRepo.save(userRole);
		
	}

	public EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate) {
		return emailTemplateRepo.save(emailTemplate);
	}

	public List<EmailTemplate> getEmailTemplateAll() {
		return StreamSupport.stream(emailTemplateRepo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	public void removeEmailTemplate(EmailTemplate emailTemplate) {
		emailTemplateRepo.delete(emailTemplate);
		
	}

	public void removeParameter(Parameter parameter) {
		parameterRepo.save(parameter);
	}

	public Parameter saveParameter(Parameter parameter) {
		return parameterRepo.save(parameter);
		
	}

	public List<Parameter> getParametersAll() {
		return StreamSupport.stream(parameterRepo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	

}
