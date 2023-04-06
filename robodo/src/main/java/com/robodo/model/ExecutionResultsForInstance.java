package com.robodo.model;

public class ExecutionResultsForInstance {
	
	public static final String STATUS_NEW="NEW";
	public static final String STATUS_SUCCESS="SUCCESS";
	public static final String STATUS_FAILED="FAILED";
	public static final String STATUS_STALLED="STALLED";
	
	ProcessInstance processInstance;
	String status;
	String message;
	
	
	public ExecutionResultsForInstance(ProcessInstance processInstance) {
		this.processInstance=processInstance;
		this.status=STATUS_NEW;
		this.message=null;
	}
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}
	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


	
	

}
