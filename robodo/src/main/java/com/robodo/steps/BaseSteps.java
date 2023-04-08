package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public abstract class BaseSteps {
	
	 RunnerUtil runnerUtil;
	 ProcessInstanceStep processInstanceStep;
	 SeleniumUtil selenium;
	 int fileOrder;
	 
	 public BaseSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		 this.runnerUtil=runnerUtil;
		 this.processInstanceStep=processInstanceStep;
		 this.selenium=new SeleniumUtil(runnerUtil);
		 this.fileOrder=1;
	 }
	 
	 public void setVariable(String key, String value) {
		runnerUtil.logger("set variable %s=[%s]".formatted(key,value)); 
		runnerUtil.setVariable(key,value);
	 }
	 
	 public String getVariable(String key) {
		 return runnerUtil.getVariable(key);
	 }
	 
	 
	 public void takeStepScreenShot(ProcessInstanceStep processInstanceStep, String description, boolean toAttach) {
		 takeStepScreenShot(processInstanceStep, description, toAttach, null);
	 }
	 
	 
	 public void takeStepScreenShot(ProcessInstanceStep processInstanceStep, String description, boolean toAttach, Runnable actionBefore) {
		 if (actionBefore!=null) {
			 actionBefore.run();
		 }
		 
		 String ssFileName = selenium.screenShot(processInstanceStep.getProcessInstance());
		 ProcessInstanceStepFile file=new ProcessInstanceStepFile();
		 file.setFileOrder(fileOrder++);
		 file.setFileName(ssFileName);
		 file.setFileType(ProcessInstanceStepFile.TYPE_SS);
		 file.setDescription(description);
		 file.setProcessInstanceStepId(processInstanceStep.getId());
		 file.setAttach(toAttach);
		 runnerUtil.processService.saveProcessInstanceStepFile(file);
	 }
	 
	 public abstract void setup();
	 public abstract void run();
	 public abstract void teardown();

}
