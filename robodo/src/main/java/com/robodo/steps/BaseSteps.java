package com.robodo.steps;

import com.robodo.runner.RunnerUtil;
import com.robodo.runner.SeleniumUtil;

public abstract class BaseSteps {
	
	 RunnerUtil runnerUtil;

	SeleniumUtil selenium;
	 
	 public BaseSteps(RunnerUtil runnerUtil) {
		 this.runnerUtil=runnerUtil;
		 this.selenium=new SeleniumUtil(runnerUtil);
	 }
	 
	 public abstract void run();

}
