package com.robodo.threads;

import java.util.List;
import java.util.stream.Collectors;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
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
		
		int queueSize=Integer.parseInt(processService.getEnv().getProperty("queue.size"));
		
		
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		List<ProcessDefinition> processDefinitionsActive=processDefinitions.stream().filter(p->p.isActive()).collect(Collectors.toList());
		
		for (ProcessDefinition processDefinition : processDefinitionsActive) {

			int length = QueueSingleton.getInstance().getQueueLength();
			int remaining = queueSize - length;		
			if (remaining<=0) {
				break;
			}
			
			List<ProcessInstance> notCompletedInstances = processService.getNotCompletedInstances(processDefinition,remaining);
				
			for (ProcessInstance processInstance : notCompletedInstances) {
				boolean isAlreadyRunning = RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode());
				
				
				if (isAlreadyRunning) {
					continue;
				}
				
				if (QueueSingleton.getInstance().inQueue(processInstance)) {
					continue;
				}
				
				
				if (processInstance.isWaitingApproval()) {
					continue;
				}
				
				QueueSingleton.getInstance().add(processInstance);
				processService.updateQueueDate(processInstance);

				remaining--;
				if (remaining<=0) {
					break;
				}
				
			}
		}
		
		
		
		
			RunnerSingleton.getInstance().stop(threadName);

	}

}
