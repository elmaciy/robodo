package com.robodo.threads;

import java.util.List;

import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.runner.RunnerUtil;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;

public class ThreadForDiscoverers implements Runnable {
	
	private ProcessService processService;
	Environment env;
	
	public ThreadForDiscoverers(ProcessService processService, Environment env) {
		this.processService=processService;
		this.env=env;
	}

	@Override
	public void run() {
		List<ProcessDefinition> processDefinitions=processService.getProcessDefinitions();
		for (ProcessDefinition processDefinition : processDefinitions) {
			if (!processDefinition.isActive()) continue;
			
			String processId="DISCOVERY.%s".formatted(processDefinition.getCode());
			
			boolean isRunning = RunnerSingleton.getInstance().hasRunningInstance(processId);
			if (isRunning) {
				System.err.println("skip :"+processId);
	            continue;
			}
			
			RunnerUtil runner = new RunnerUtil(processService, env);
			
			RunnerSingleton.getInstance().start(processId);
			
			List<ProcessInstance> discoveredInstances = runner.runProcessDiscovery(processDefinition);
			for (ProcessInstance discoveredInstance : discoveredInstances) {
				System.err.println("discovered : new instance [%s] of process [%s]".formatted(processDefinition.getCode(),discoveredInstance.getCode()));
				boolean isExists=processService.isProcessInstanceAlreadyExists(discoveredInstance);
				if (isExists) {
					System.err.println("skip process [%s]/%s".formatted(processDefinition.getCode(),discoveredInstance.getCode(), processDefinition.getCode()));
					continue;
				}
				processService.saveProcessInstance(discoveredInstance);
			}
			
			RunnerSingleton.getInstance().stop(processId);	
		}
		
	}

}
