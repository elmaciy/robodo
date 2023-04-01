package com.robodo.discoverer;

import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public abstract class BaseDiscoverer {
	
	RunnerUtil runnerUtil;
	SeleniumUtil selenium;
	
	public BaseDiscoverer(RunnerUtil runnerUtil) {
		this.runnerUtil=runnerUtil;
		this.selenium=new SeleniumUtil(runnerUtil);
	}
	
	public abstract List<ProcessInstance> discover(ProcessDefinition processDefinition);

}
