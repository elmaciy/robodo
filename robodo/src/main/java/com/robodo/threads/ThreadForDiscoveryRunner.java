package com.robodo.threads;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class ThreadForDiscoveryRunner implements Runnable {
	
	private ProcessService processService;
	private ProcessDefinition processDefinition;
	
	public ThreadForDiscoveryRunner(ProcessService processService, ProcessDefinition processDefinition) {
		this.processService=processService;
		this.processDefinition=processDefinition;
	}

	@Override
	public void run() {
		if (!processDefinition.isActive()) return;
		
		String processId="DISCOVERY.%s".formatted(processDefinition.getCode());
		
		boolean isRunning = RunnerSingleton.getInstance().hasRunningInstance(processId);
		if (isRunning) {
            return;
		}

		RunnerUtil runner = new RunnerUtil(processService);
		runner.logger("start discovery for process : %s, running class : %s".formatted(processDefinition.getCode(), processDefinition.getDiscovererClass()));
		
		RunnerSingleton.getInstance().start(processId);
		
		processDefinition.setStatus(ProcessDefinition.STATUS_RUNNING);
		processDefinition.setStarted(LocalDateTime.now());
		processDefinition.setFinished(null);
		processDefinition.setLogs("");
		
		processService.saveProcessDefinition(processDefinition);
		
		boolean hasError=false;
		
		
		int discovered=0;
		try {
			List<ProcessInstance> discoveredInstances = runner.runProcessDiscovery(processDefinition);
			for (ProcessInstance discoveredInstance : discoveredInstances) {
				runner.logger("discovered : new instance [%s] of process [%s]".formatted(processDefinition.getCode(),discoveredInstance.getCode()));
				boolean isExists=processService.isProcessInstanceAlreadyExists(discoveredInstance);
				if (isExists) {
					runner.logger("skip instance [%s]/%s".formatted(processDefinition.getCode(),discoveredInstance.getCode(), processDefinition.getCode()));
					continue;
				}
				
				processService.saveProcessInstance(discoveredInstance);
				discovered++;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			runner.logger("Exception on discovery class [%s] of the process [%s] : ".formatted(processDefinition.getDiscovererClass(), processDefinition.getCode()));
			runner.logger(e.getMessage());
			hasError=true;
		}

		runner.logger("finished discovery for process : %s".formatted(processDefinition.getCode()));
		runner.logger("%d instance discovered.".formatted(discovered));

		
		processDefinition.setLogs(runner.getLogs());
		processDefinition.setFinished(LocalDateTime.now());
		processDefinition.setStatus(hasError ? ProcessDefinition.STATUS_FAILED : ProcessDefinition.STATUS_COMPLETED);
		processService.saveProcessDefinition(processDefinition);

		
		RunnerSingleton.getInstance().stop(processId);	
	}



}
