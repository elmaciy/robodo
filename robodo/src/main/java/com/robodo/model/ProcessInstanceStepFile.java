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
@Table(name = "process_instance_step_files")
public class ProcessInstanceStepFile {

	public static final String TYPE_SS="SCREENSHOT";
	public static final String TYPE_DOWNLOADED="DOWNLOADED";
	public static final String TYPE_OTHER="OTHER";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String fileType;
	@Column(length = 1000)
	String description;
	@Column(length = 1000)
	String fileName;
	boolean attach;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "process_instance_step_id", nullable = false)
	ProcessInstanceStep processInstanceStep;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProcessInstanceStep getProcessInstanceStep() {
		return processInstanceStep;
	}

	public void setProcessInstanceStep(ProcessInstanceStep processInstanceStep) {
		this.processInstanceStep = processInstanceStep;
	}

	public boolean isAttach() {
		return attach;
	}

	public void setAttach(boolean attach) {
		this.attach = attach;
	}
	


}
