package com.robodo.base;

import com.robodo.model.ProcessInstanceStep;
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
	 
	 public String getEnvironmentParameter(String key) {
		 return runnerUtil.getEnvironmentParameter(key);
	 }
	 
	 
	 public abstract void setup();
	 public abstract void run();
	 public abstract void teardown();

}
