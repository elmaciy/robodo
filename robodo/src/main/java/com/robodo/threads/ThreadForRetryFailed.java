package com.robodo.threads;

import java.util.List;
import java.util.stream.Collectors;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.HelperUtil;

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
				processService.deleteAllFiles(instance);
				instance.retryProcessInstance(processService);
				//retryInstanceStep(instance);
				processService.saveProcessInstance(instance);
			});
		}
		
		RunnerSingleton.getInstance().stop(threadName);

	}

	

}
