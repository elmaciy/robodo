package com.robodo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_instances")
public class ProcessInstance {
	public static final String STATUS_NEW="NEW";
	public static final String STATUS_RUNNING="RUNNING";
	public static final String STATUS_HUMAN="HUMAN";
	public static final String BEGIN = "<BEGIN>";
	public static final String END = "<END>";

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String code;
	String description;
	int retryNo;
	LocalDateTime created;
	LocalDateTime started;
	LocalDateTime finished;
	String status;
	String currentStepCode;
	@Column(length = 65000)
	String instanceVariables;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "process_definition_id", nullable = false)
	ProcessDefinition processDefinition;
	
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
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRetryNo() {
		return retryNo;
	}

	public void setRetryNo(int retryNo) {
		this.retryNo = retryNo;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}



	public String getCurrentStepCode() {
		return currentStepCode;
	}

	public void setCurrentStepCode(String currentStepCode) {
		this.currentStepCode = currentStepCode;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public List<ProcessInstanceStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ProcessInstanceStep> steps) {
		this.steps = steps;
	}

	public String getInstanceVariables() {
		return instanceVariables;
	}

	public void setInstanceVariables(String instanceVariables) {
		this.instanceVariables = instanceVariables;
	}

	
	
	
	
	

}
