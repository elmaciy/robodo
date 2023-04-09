package com.robodo.threads;

import java.util.List;
import java.util.stream.Collectors;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.QueueSingleton;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.singleton.ThreadGroupSingleton;
import com.robodo.utils.HelperUtil;

public class ThreadForQueueManager implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForQueueManager(ProcessService processService) {
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

				List<ProcessInstance> notCompletedInstances = processService.getNotCompletedInstances(processDefinition,100);
				
				for (ProcessInstance processInstance : notCompletedInstances) {
					boolean isAlreadyRunning = RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode());
					
					
					if (isAlreadyRunning) {
						continue;
					}
					
					if (QueueSingleton.getInstance().inQueue(processInstance)) {
						continue;
					}
					
					QueueSingleton.getInstance().add(processInstance);
				}
			}
			
		
		
		
			RunnerSingleton.getInstance().stop(threadName);

	}

}
