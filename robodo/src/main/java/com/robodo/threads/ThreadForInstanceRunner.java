package com.robodo.threads;

import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.utils.RunnerUtil;

public class ThreadForInstanceRunner implements Runnable {
	
	private ProcessService processService;
	ProcessInstance processInstance;
	
	public ThreadForInstanceRunner(ProcessService processService, ProcessInstance processInstance) {
		this.processService=processService;
		this.processInstance=processInstance;
	}

	@Override
	public void run() {
		RunnerUtil runner=new RunnerUtil(processService);
		runner.logger("start task : %s".formatted(processInstance.getCode()));
		runner.runProcessInstance(processInstance);
		runner.logger("end task : %s".formatted(processInstance.getCode()));
	}

}
