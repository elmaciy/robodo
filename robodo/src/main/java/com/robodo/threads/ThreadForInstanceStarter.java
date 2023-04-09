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

public class ThreadForInstanceStarter implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForInstanceStarter(ProcessService processService) {
		this.processService=processService;
	}

	@Override
	public void run() {
		
		String threadName=this.getClass().getName();
		
		if (RunnerSingleton.getInstance().hasRunningInstance(threadName)) {
			return;
		}
		
		RunnerSingleton.getInstance().start(threadName);
		
		
		int maxThreadCount=Integer.valueOf(processService.getEnv().getProperty("max.thread"));
						
		while(true) {

			ProcessInstance processInstance=QueueSingleton.getInstance().get();
			if (processInstance==null) {
				break;
			}
			
			String threadGroupName=processInstance.getProcessDefinition().getCode();
			ThreadGroup thGroup=ThreadGroupSingleton.getInstance().getThreadGroupByName(threadGroupName);
			int activeInstancesCount=ThreadGroupSingleton.getInstance().filteredActiveThreadCount(thGroup);
			
			int remaining=processInstance.getProcessDefinition().getMaxThreadCount()-activeInstancesCount;
			
			if (remaining<=0) {
				break;
			}
			

			boolean isAlreadyRunning = RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode());
			
			if (isAlreadyRunning) {
				continue;
			}
			
			
			Thread th=new Thread(thGroup, new ThreadForInstanceRunner(processService, processInstance));
			th.start();
			HelperUtil.sleep(300L);

			QueueSingleton.getInstance().remove(processInstance);
			
			
			int activeThreadCount=ThreadGroupSingleton.getInstance().getActiveThreadCount();

			if (activeThreadCount>=maxThreadCount) {
				break;
			}
		}

		RunnerSingleton.getInstance().stop(threadName);

	}

}
