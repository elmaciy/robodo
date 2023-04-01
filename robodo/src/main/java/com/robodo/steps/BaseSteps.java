package com.robodo.steps;

import java.util.HashMap;

import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public abstract class BaseSteps {
	
	 RunnerUtil runnerUtil;

	SeleniumUtil selenium;
	 
	 public BaseSteps(RunnerUtil runnerUtil) {
		 this.runnerUtil=runnerUtil;
		 this.selenium=new SeleniumUtil(runnerUtil);
	 }
	 
	 public void setVariable(String key, String value) {
			runnerUtil.setVariable(key,value);
	 }
	 
	 public String getVariable(String key) {
		 return runnerUtil.getVariable(key);
	 }
	 
	 public abstract void run();

}
