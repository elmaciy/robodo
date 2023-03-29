package com.robodo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_definition")
public class ProcessDefinition {
		
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	String code;
	String description;
	@OneToMany(mappedBy = "processDefinition", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<ProcessDefinitionStep> steps;
	String discovererClass;
	Integer maxRetryCount;
	Integer maxThreadCount;
	boolean isSingleAtATime;
	boolean isActive;

	
	@OneToMany(mappedBy = "processDefinition", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<ProcessInstance> instances;
	
	
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
	public List<ProcessDefinitionStep> getSteps() {
		return steps;
	}
	public void setSteps(List<ProcessDefinitionStep> steps) {
		this.steps = steps;
	}
	public Integer getMaxRetryCount() {
		return maxRetryCount;
	}
	public void setMaxRetryCount(Integer maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}
	public List<ProcessInstance> getInstances() {
		return instances;
	}
	public void setInstances(List<ProcessInstance> instances) {
		this.instances = instances;
	}
	public boolean isSingleAtATime() {
		return isSingleAtATime;
	}
	public void setSingleAtATime(boolean isSingleAtATime) {
		this.isSingleAtATime = isSingleAtATime;
	}
	public Integer getMaxThreadCount() {
		return maxThreadCount;
	}
	public void setMaxThreadCount(Integer maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}
	public String getDiscovererClass() {
		return discovererClass;
	}
	public void setDiscovererClass(String discovererClass) {
		this.discovererClass = discovererClass;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	
	
}
