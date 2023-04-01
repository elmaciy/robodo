package com.robodo.threads;

import org.springframework.core.env.Environment;

import com.robodo.model.ExecutionResultsForInstance;
import com.robodo.model.ProcessInstance;
import com.robodo.runner.RunnerUtil;
import com.robodo.services.ProcessService;

public class ThreadForInstanceRunner implements Runnable {
	
	private ProcessService processService;
	Environment env;
	ProcessInstance processInstance;
	
	public ThreadForInstanceRunner(ProcessService processService, Environment env, ProcessInstance processInstance) {
		this.processService=processService;
		this.env=env;
		this.processInstance=processInstance;
	}

	@Override
	public void run() {
		RunnerUtil runner=new RunnerUtil(processService);
		runner.logger("running task : %s".formatted(processInstance.getCode()));
		ExecutionResultsForInstance result = runner.runProcessInstance(processInstance);
		if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_FAILED)) {
			String msg=result.getMessage();
			runner.logger("exception at task : %s => %s".formatted(processInstance.getCode(),msg));
			return;
		}
		else  if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_NOT_ELIGIBLE)) {
			runner.logger("the instance [%s,%s] is not eligible for running at the moment, possibbly due to the limitations".formatted(processInstance.getCode()));
		} else {
			processService.saveProcessInstance(result.getProcessInstance());
		}
		

	}

}
