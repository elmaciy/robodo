package com.robodo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_definition_steps")
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


	

}
