package com.robodo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_instance_step", 
indexes = {
		@Index(name="ndx_instance_step_instance_id", columnList = "process_instance_id"),
		@Index(name="ndx_instance_step_step_code", columnList = "stepCode"),
		@Index(name="ndx_instance_step_status", columnList = "status")
			}
)
public class ProcessInstanceStep {
	
	public static final String STATUS_NEW="NEW";
	public static final String STATUS_RUNNING="RUNNING";
	public static final String STATUS_FAILED="FAILED";
	public static final String STATUS_COMPLETED="COMPLETED";
	public static final String STEP_NONE = "NONE";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String stepCode;
	String status;
	String orderNo;
	@Column(length = 4000)
	String commands;
	LocalDateTime created;
	LocalDateTime started;
	LocalDateTime finished;
	@Column(columnDefinition = "mediumtext")
	String logs;
	@Column(columnDefinition = "mediumtext")
	String error;
	@Column(columnDefinition = "mediumtext")
	String instanceVariables;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "process_instance_id", nullable = false)
	ProcessInstance processInstance;
	
	boolean notificationSent;
	boolean approved;
	String approvedBy;
	LocalDateTime approvalDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public String getCommands() {
		return commands;
	}

	public void setCommands(String commands) {
		this.commands = commands;
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public boolean isNotificationSent() {
		return notificationSent;
	}

	public void setNotificationSent(boolean notificationSent) {
		this.notificationSent = notificationSent;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public LocalDateTime getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(LocalDateTime approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	public boolean isHumanInteractionStep() {
		return this.getCommands().startsWith("waitHumanInteraction");
	}

	public String getInstanceVariables() {
		return instanceVariables;
	}

	public void setInstanceVariables(String instanceVariables) {
		this.instanceVariables = instanceVariables;
	}

	public boolean isFinished() {
		return this.getStatus().equals(STATUS_COMPLETED) || this.getStatus().equals(STATUS_FAILED);
	}
	
	
	
	

}
