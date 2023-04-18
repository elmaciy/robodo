package com.robodo.base;

import java.util.HashMap;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.Tokenization;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public abstract class BaseStep {
	
	 protected RunnerUtil runnerUtil;
	 protected ProcessInstanceStep processInstanceStep;
	 
	 public BaseStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		 this.runnerUtil=runnerUtil;
		 this.processInstanceStep=processInstanceStep;
	 }
	 
	 public void setVariable(String key, String value) {
		runnerUtil.logger("set variable %s=[%s]".formatted(key,value)); 
		runnerUtil.setVariable(key,value);
	 }
	 
	 public String getVariable(String key) {
		 return runnerUtil.getVariable(key);
	 }
	 
	public void createApprovalLinks(HashMap<String, String> hmVars, String processInstanceCode) {
		String encodedProcessInstanceCode=HelperUtil.encrypt(processInstanceCode);
		String serverHost=runnerUtil.getEnvironmentParameter("server.host.path");
		long tokenDuration=Long.valueOf(runnerUtil.processService.getEnvProperty("token.duration"));

		Tokenization token=Tokenization.generateNewToken(runnerUtil.processService, "APPROVAL", processInstanceCode, tokenDuration);
		String tokenKey=token.getToken();
		
		hmVars.put("LINK.APPROVE", 	"%s/approve/%s/APPROVE/EMAIL/%s".formatted(serverHost, encodedProcessInstanceCode, tokenKey));
		hmVars.put("LINK.DECLINE", 	"%s/approve/%s/DECLINE/EMAIL/%s".formatted(serverHost, encodedProcessInstanceCode, tokenKey));
		hmVars.put("LINK.VIEW", 	"%s/approve/%s/vıew/EMAIL/%s"	.formatted(serverHost, encodedProcessInstanceCode, tokenKey));
		
	}
	 
	 
	 public abstract void setup();
	 public abstract void run();
	 public abstract void teardown();

}
