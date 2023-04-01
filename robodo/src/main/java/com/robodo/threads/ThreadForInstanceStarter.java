package com.robodo.threads;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.ThreadGroupSingleton;

public class ThreadForInstanceStarter implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForInstanceStarter(ProcessService processService) {
		this.processService=processService;
	}

	@Override
	public void run() {
		
		int maxThreadCount=Integer.valueOf(processService.getEnv().getProperty("max.thread"));
		int currentThreadCount=ThreadGroupSingleton.getInstance().getActiveThreadCount();
		
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		List<ProcessDefinition> processDefinitionsActive=processDefinitions.stream().filter(p->p.isActive()).collect(Collectors.toList());
		for (ProcessDefinition processDefinition : processDefinitionsActive) {
			
			String threadGroupName=processDefinition.getCode();
			ThreadGroup thGroup=ThreadGroupSingleton.getInstance().getThreadGroupByName(threadGroupName);
			int activeInstancesCount=thGroup.activeCount();
			int remaining=processDefinition.getMaxThreadCount()-activeInstancesCount;
			if (remaining>0) {
				List<ProcessInstance> notCompletedInstances = processService.getProcessNotCompletedInstances(processDefinition,remaining);
				for (ProcessInstance processInstance : notCompletedInstances) {
					Thread th=new Thread(new ThreadForInstanceRunner(processService, processInstance));
					th.start();
					currentThreadCount++;
					if (currentThreadCount>=maxThreadCount) {
						break;
					}
				}
			}
			
			if (currentThreadCount>=maxThreadCount) {
				break;
			}
		
		}

	}

}
