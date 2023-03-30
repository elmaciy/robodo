package com.robodo.threads;

import java.util.List;

import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.ThreadGroupSingleton;

public class ThreadForInstanceStarter implements Runnable {
	
	private ProcessService processService;
	Environment env;
	
	public ThreadForInstanceStarter(ProcessService processService, Environment env) {
		this.processService=processService;
		this.env=env;
	}

	@Override
	public void run() {
		
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		processDefinitions.stream().filter(p->p.isActive()).forEach(processDefinition->{
			String threadGroupName=processDefinition.getCode();
			ThreadGroup thGroup=ThreadGroupSingleton.getInstance().getThreadGroupByName(threadGroupName);
			int activeInstancesCount=thGroup.activeCount();
			int remaining=processDefinition.getMaxThreadCount()-activeInstancesCount;
			if (remaining>0) {
				List<ProcessInstance> notCompletedInstances = processService.getProcessNotCompletedInstances(processDefinition,remaining);
				for (ProcessInstance processInstance : notCompletedInstances) {
					Thread th=new Thread(new ThreadForInstanceRunner(processService, env, processInstance));
					th.start();
				}
			}
		});
	}

}
