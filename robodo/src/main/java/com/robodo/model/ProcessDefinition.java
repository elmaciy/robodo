package com.robodo.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
	private List<ProcessDefinitionStep> steps;
	String discovererClass;
	Integer maxAttemptCount;
	Integer maxThreadCount;
	boolean isActive;
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
	public List<ProcessDefinitionStep> getSteps() {
		Collections.sort(steps, new Comparator<ProcessDefinitionStep>() {

			@Override
			public int compare(ProcessDefinitionStep o1, ProcessDefinitionStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}
		});
		return steps;
	}
	
	public void setSteps(List<ProcessDefinitionStep> steps) {
		this.steps = steps;
	}
	public Integer getMaxAttemptCount() {
		return maxAttemptCount;
	}
	public void setMaxAttemptCount(Integer maxAttemptCount) {
		this.maxAttemptCount = maxAttemptCount;
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
	
	public String getNextStepOrder() {
		if (this.getSteps().size()==0) {
			return "01";
		}
		
		ProcessDefinitionStep lastStep = this.getSteps().get(this.getSteps().size()-1);
		return StringUtils.right("0000%d".formatted(Integer.valueOf(lastStep.getOrderNo())+1), 2);
	}
	
}
