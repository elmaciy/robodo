package com.robodo.model;

public class ExecutionResultsForCommand {
	
	public static final String STATUS_SUCCESS="SUCCESS";
	public static final String STATUS_FAILED="FAILED";
	public static final String STATUS_SKIPPED="SKIPPED";
	
	String status;
	String message;

	
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
	public ExecutionResultsForCommand failed() {
		this.setStatus(STATUS_FAILED);
		return this;
	}
	
	public ExecutionResultsForCommand succeeded() {
		this.setStatus(STATUS_SUCCESS);
		return this;
	}
	public ExecutionResultsForCommand skipped() {
		this.setStatus(STATUS_SKIPPED);
		return null;
	}
	public ExecutionResultsForCommand withMessage(String message) {
		this.setMessage(message);
		return this;
	}
	
	

}
