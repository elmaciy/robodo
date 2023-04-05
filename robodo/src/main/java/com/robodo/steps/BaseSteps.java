package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public abstract class BaseSteps {
	
	 RunnerUtil runnerUtil;
	 ProcessInstanceStep processInstanceStep;
	 SeleniumUtil selenium;
	 
	 public BaseSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		 this.runnerUtil=runnerUtil;
		 this.processInstanceStep=processInstanceStep;
		 this.selenium=new SeleniumUtil(runnerUtil);
	 }
	 
	 public void setVariable(String key, String value) {
		runnerUtil.logger("set variable %s=[%s]".formatted(key,value)); 
		runnerUtil.setVariable(key,value);
	 }
	 
	 public String getVariable(String key) {
		 return runnerUtil.getVariable(key);
	 }
	 
	 public void takeStepScreenShot(ProcessInstanceStep processInstanceStep, String description, boolean toAttach) {
		 String ssFileName = selenium.screenShot(processInstanceStep.getProcessInstance());
		 ProcessInstanceStepFile file=new ProcessInstanceStepFile();
		 file.setFileName(ssFileName);
		 file.setFileType(ProcessInstanceStepFile.TYPE_SS);
		 file.setDescription(description);
		 file.setProcessInstanceStep(processInstanceStep);
		 file.setAttach(toAttach);
		 processInstanceStep.getFiles().add(file);
	 }
	 
	 public abstract void run();

}
