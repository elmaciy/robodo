package com.robodo.threads;

import java.util.List;
import java.util.stream.Collectors;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;

public class ThreadForRetryFailed implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForRetryFailed(ProcessService processService) {
		this.processService=processService;
	}

	@Override
	public void run() {
		
		String threadName=this.getClass().getName();
		
		if (RunnerSingleton.getInstance().hasRunningInstance(threadName)) {
			return;
		}
		
		RunnerSingleton.getInstance().start(threadName);
		
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		List<ProcessDefinition> processDefinitionsActive=processDefinitions.stream().filter(p->p.isActive()).collect(Collectors.toList());
		
		for (ProcessDefinition processDefinition : processDefinitionsActive) {
			List<ProcessInstance> instances = processService.getProcessFailedAndToBeRetriedInstances(processDefinition, processDefinition.getMaxThreadCount());
			instances.forEach(instance->{
				//retryInstance(instance);
				retryInstanceStep(instance);
				processService.deleteAllFiles(instance);
				processService.saveProcessInstance(instance);
			});
		}
		
		RunnerSingleton.getInstance().stop(threadName);

	}

	private void retryInstance(ProcessInstance instance) {
		instance.setError(null);
		instance.setFailed(false);
		instance.setStarted(null);
		instance.setFinished(null);
		instance.setStatus(ProcessInstance.STATUS_RETRY);
		
		for (var step : instance.getSteps()) {
			step.setApprovalDate(null);
			step.setApprovedBy(null);
			step.setApproved(false);
			step.setError(null);
			step.setLogs(null);
			step.setStarted(null);
			step.setFinished(null);
			step.setNotificationSent(false);
			step.setStatus(ProcessInstanceStep.STATUS_NEW);
			
		}
		
	}
	
	
	private void retryInstanceStep(ProcessInstance instance) {
		
		
		ProcessInstanceStep latestProcessedStep = instance.getLatestProcessedStep();
		if (latestProcessedStep==null) {
			return;
		}
		
		instance.setError(null);
		instance.setFailed(false);
		instance.setFinished(null);
		instance.setStatus(ProcessInstance.STATUS_RUNNING);

		latestProcessedStep.setApprovalDate(null);
		latestProcessedStep.setApprovedBy(null);
		latestProcessedStep.setApproved(false);
		latestProcessedStep.setError(null);
		latestProcessedStep.setLogs(null);
		latestProcessedStep.setStarted(null);
		latestProcessedStep.setFinished(null);
		latestProcessedStep.setNotificationSent(false);
		latestProcessedStep.setStatus(ProcessInstanceStep.STATUS_NEW);
		
		
	}

}
