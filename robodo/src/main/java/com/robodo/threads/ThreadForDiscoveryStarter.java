package com.robodo.threads;

import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.HelperUtil;

public class ThreadForDiscoveryStarter implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForDiscoveryStarter(ProcessService processService) {
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
						
			Thread th=new Thread(new ThreadForDiscoveryRunner(processService, processDefinition));
			th.start();
			HelperUtil.sleep(300L);
			
		}
		
		RunnerSingleton.getInstance().stop(threadName);
		
		
	}



}
