package com.robodo.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.robodo.services.ProcessService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_instance", 
	indexes = {
			@Index(name="ndx_instance_code", columnList = "code", unique = true),
			@Index(name="ndx_instance_status", columnList = "status")
				}
)
public class ProcessInstance {
	public static final String STATUS_NEW="NEW";
	public static final String STATUS_RETRY="RETRY";
	public static final String STATUS_RUNNING="RUNNING";
	public static final String STATUS_COMPLETED ="COMPLETED";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String code;
	String description;
	int attemptNo;
	LocalDateTime created;
	LocalDateTime started;
	LocalDateTime finished;
	LocalDateTime queued;
	String status;
	@Column(columnDefinition = "mediumtext")
	String error;
	boolean failed=false;
	@Column(columnDefinition = "mediumtext")
	String initialInstanceVariables;
	@Column(columnDefinition = "mediumtext")
	String instanceVariables;	
	Long processDefinitionId;
	String token;
	
	@OneToMany(mappedBy = "processInstance", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<ProcessInstanceStep> steps;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		if (this.token==null) {
			this.token =UUID.randomUUID().toString();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAttemptNo() {
		return attemptNo;
	}

	public void setAttemptNo(int attemptNo) {
		this.attemptNo = attemptNo;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public void setStarted(LocalDateTime started) {
		this.started = started;
	}

	public LocalDateTime getFinished() {
		return finished;
	}

	public void setFinished(LocalDateTime finished) {
		this.finished = finished;
	}

	public LocalDateTime getQueued() {
		return queued;
	}

	public void setQueued(LocalDateTime queued) {
		this.queued = queued;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(Long processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public List<ProcessInstanceStep> getSteps() {
		
		Collections.sort(steps, new Comparator<ProcessInstanceStep>() {

			@Override
			public int compare(ProcessInstanceStep o1, ProcessInstanceStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}
		});
		
		return steps;
	}

	public void setSteps(List<ProcessInstanceStep> steps) {
		this.steps = steps;
	}

	public String getInitialInstanceVariables() {
		return initialInstanceVariables;
	}

	public void setInitialInstanceVariables(String initialInstanceVariables) {
		this.initialInstanceVariables = initialInstanceVariables;
	}

	public String getInstanceVariables() {
		return instanceVariables;
	}

	public void setInstanceVariables(String instanceVariables) {
		this.instanceVariables = instanceVariables;
	}
	

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ProcessInstanceStep getCurrentStep() {
		if (this.getStatus().equals(STATUS_NEW) || this.getStatus().equals(STATUS_RETRY)) {
			return null;
		}
		
		if (this.getStatus().equals(STATUS_COMPLETED)) {
			return this.getLatestProcessedStep();
		}
		
		List<ProcessInstanceStep> runningSteps = this.steps.stream()
				.filter(p->p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING) || p.getStatus().equals(ProcessInstanceStep.STATUS_FAILED))
				.collect(Collectors.toList());
		
		if (runningSteps.size()==0) {
			return null;
		}
		
		return runningSteps.get(runningSteps.size()-1);
 	}
	
	public ProcessInstanceStep getLatestProcessedStep() {
		int k=-1;
		var steps=this.getSteps();
		
		for (int i=0;i<steps.size();i++) {
			ProcessInstanceStep step = steps.get(i);
			if (step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
				break;
			} 
			k=i;
		}
		
		return k==-1 ? null : steps.get(k);
	}

	public List<ProcessInstanceStep> getPreviousSteps(ProcessInstanceStep refStep) {
		return this.steps.stream().filter(p->p.getOrderNo().compareTo(refStep.getOrderNo())<0)
				.collect(Collectors.toList());
	}

	public List<ProcessInstanceStep> getNextSteps(ProcessInstanceStep refStep) {
		return this.steps.stream().filter(p->p.getOrderNo().compareTo(refStep.getOrderNo())>0)
				.collect(Collectors.toList());
	}

	public boolean isWaitingApproval() {
		if (!this.status.equals(STATUS_RUNNING)) {
			return false;
		}
		
		ProcessInstanceStep currentStep = getCurrentStep();
		if (currentStep==null) {
			return false;
		}
		
		return 	currentStep.isHumanInteractionStep() && 
				currentStep.isNotificationSent() &&
				currentStep.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING) && 
				!currentStep.isApproved();
	}

	public boolean isTheLatestStep(ProcessInstanceStep step) {
		ProcessInstanceStep latestProcessedStep = this.getLatestProcessedStep();
		if (latestProcessedStep==null) {
			return false;
		}
		return latestProcessedStep.getStepCode().equals(step.getStepCode());
	}
	
	public ProcessInstanceStep getFirstStep() {
		return this.getSteps().stream().findFirst().get();
	}

	public boolean isFinished() {
		return this.getStatus().equals(STATUS_COMPLETED);
	}

	public boolean isInitialStatus() {
		return this.getStatus().equals(STATUS_NEW) || this.getStatus().equals(STATUS_RETRY);
	}

	public boolean toBeRetried() {
		return this.getStatus().equals(STATUS_RETRY);
	}

	public void retryProcessInstance(ProcessService processService) {

		this.setError(null);
		this.setFailed(false);
		this.setStarted(null);
		this.setFinished(null);
		this.setStatus(ProcessInstance.STATUS_RETRY);
		this.setQueued(null);
		this.setAttemptNo(Integer.max(0, this.getAttemptNo()-1));
		
		
		for (var step : this.getSteps()) {
			step.setApprovalDate(null);
			step.setApprovedBy(null);
			step.setApproved(false);
			step.setError(null);
			step.setLogs(null);
			step.setStarted(null);
			step.setFinished(null);
			step.setNotificationSent(false);
			step.setStatus(ProcessInstanceStep.STATUS_NEW);
			
		}
		
	
		
	}

	


}
