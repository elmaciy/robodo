package com.robodo.threads;

import com.robodo.model.ExecutionResultsForInstance;
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
		
		ExecutionResultsForInstance result = runner.runProcessInstance(processInstance);
		
		if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_FAILED)) {
			String msg=result.getMessage();
			runner.logger("exception at task : %s => %s".formatted(processInstance.getCode(),msg));
			return;
		}
		else  if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_NOT_ELIGIBLE)) {
			runner.logger("the instance %s is not eligible for running at the moment, possibbly due to the limitations".formatted(processInstance.getCode()));
		} else {
			processService.saveProcessInstance(result.getProcessInstance());
		}
		
		runner.logger("end task : %s".formatted(processInstance.getCode()));

		

	}

}
