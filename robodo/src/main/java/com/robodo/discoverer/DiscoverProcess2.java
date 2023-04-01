package com.robodo.discoverer;

import java.util.ArrayList;
import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.utils.RunnerUtil;

public class DiscoverProcess2 extends BaseDiscoverer {

	public DiscoverProcess2(RunnerUtil runnerUtil) {
		super(runnerUtil);
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
		
		return instances;
	}

}
