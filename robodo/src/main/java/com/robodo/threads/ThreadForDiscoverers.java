package com.robodo.threads;

import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.RunnerUtil;

public class ThreadForDiscoverers implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForDiscoverers(ProcessService processService) {
		this.processService=processService;
	}

	@Override
	public void run() {
		
		String threadName=this.getClass().getName();
		
		if (RunnerSingleton.getInstance().hasRunningInstance(threadName)) {
			return;
		}
		
		RunnerSingleton.getInstance().start(threadName);
		
		List<ProcessDefinition> processDefinitions=processService.getProcessDefinitions();
		for (ProcessDefinition processDefinition : processDefinitions) {
			if (!processDefinition.isActive()) continue;
			
			String processId="DISCOVERY.%s".formatted(processDefinition.getCode());
			
			boolean isRunning = RunnerSingleton.getInstance().hasRunningInstance(processId);
			if (isRunning) {
				System.err.println("skip :"+processId);
	            continue;
			}
			
			RunnerUtil runner = new RunnerUtil(processService);
			
			RunnerSingleton.getInstance().start(processId);
			
			List<ProcessInstance> discoveredInstances = runner.runProcessDiscovery(processDefinition);
			for (ProcessInstance discoveredInstance : discoveredInstances) {
				runner.logger("discovered : new instance [%s] of process [%s]".formatted(processDefinition.getCode(),discoveredInstance.getCode()));
				boolean isExists=processService.isProcessInstanceAlreadyExists(discoveredInstance);
				if (isExists) {
					runner.logger("skip process [%s]/%s".formatted(processDefinition.getCode(),discoveredInstance.getCode(), processDefinition.getCode()));
					continue;
				}
				
				processService.saveProcessInstance(discoveredInstance);
			}
			
			RunnerSingleton.getInstance().stop(processId);	
		}
		
		RunnerSingleton.getInstance().stop(threadName);
		
		
	}

}
