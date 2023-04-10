package com.robodo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_definition_step")
public class ProcessDefinitionStep {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String code;
	String description;
	boolean isSingleAtATime;
	String orderNo;
	@Column(length = 4000)
	String commands;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "process_definition_id", nullable = false)
	ProcessDefinition processDefinition;
	LocalDateTime created;
	LocalDateTime updated;
	
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

	public boolean isSingleAtATime() {
		return isSingleAtATime;
	}

	public void setSingleAtATime(boolean isSingleAtATime) {
		this.isSingleAtATime = isSingleAtATime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCommands() {
		return commands;
	}

	public void setCommands(String commands) {
		this.commands = commands;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	@PrePersist
	protected void onCreate() {
		this.created = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated= LocalDateTime.now();
	}
	

}
