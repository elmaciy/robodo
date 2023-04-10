package com.robodo.model;

import java.sql.Blob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "process_instance_step_file", 
indexes = {
		@Index(name="ndx_instance_step_file_instance_id", columnList = "processInstanceStepId")
			}
)
public class ProcessInstanceStepFile {

	public static final String TYPE_SS="SCREENSHOT";
	public static final String TYPE_OTHER="OTHER";
	public static final String MIME_TYPE_SCREENSHOT = "image/png";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String fileType;
	int fileOrder;
	@Column(length = 1000)
	String description;
	boolean attach;
	@Column(columnDefinition = "longblob")
	Blob binarycontent;
	String mimeType;
	
	Long processInstanceStepId;

	LocalDateTime created;
	
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public Long getProcessInstanceStepId() {
		return processInstanceStepId;
	}

	public void setProcessInstanceStepId(Long processInstanceStepId) {
		this.processInstanceStepId = processInstanceStepId;
	}

	public boolean isAttach() {
		return attach;
	}

	public void setAttach(boolean attach) {
		this.attach = attach;
	}

	public int getFileOrder() {
		return fileOrder;
	}

	public void setFileOrder(int fileOrder) {
		this.fileOrder = fileOrder;
	}

	public Blob getBinarycontent() {
		return binarycontent;
	}

	public void setBinarycontent(Blob binarycontent) {
		this.binarycontent = binarycontent;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	@PrePersist
	protected void onCreate() {
		this.created = LocalDateTime.now();
	}

}
