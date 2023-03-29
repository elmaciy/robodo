package com.robodo.threads;

import java.util.List;

import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.runner.RunnerUtil;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;

public class ThreadForSteps implements Runnable {
	
	private ProcessService processService;
	Environment env;
	
	public ThreadForSteps(ProcessService processService, Environment env) {
		this.processService=processService;
		this.env=env;
	}

	@Override
	public void run() {
		List<ProcessInstance> processInstances=processService.getProcessNotCompletedInstances(2);
		if (processInstances.isEmpty()) {
			System.err.println("no process instance to run");
			return;
		}
		RunnerUtil runner = new RunnerUtil(env);

		for (ProcessInstance processInstance : processInstances) {
			System.err.println("running task : %s".formatted(processInstance.getCode()));
			ProcessInstance processInstanceAfterRun = runner.runProcessInstance(processInstance);
			if (processInstanceAfterRun == null) {
				System.err.println("exception at task : %s/%s".formatted(processInstance.getCode(),processInstance.getProcessDefinition().getCode()));
				continue;
			}
			
			processService.saveProcessInstance(processInstanceAfterRun);
		}

		
	}

}
