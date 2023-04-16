package com.robodo.threads;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.robodo.singleton.QueueSingleton;
import com.robodo.singleton.RunnerSingleton;
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
		
		
		int maxProcessCount=Integer.valueOf(processService.getEnvProperty("max.thread"));
						
		while(true) {

			ProcessInstance processInstance=QueueSingleton.getInstance().get();
			
			if (processInstance==null) {
				break;
			}

			ProcessDefinition processDefinition=processService.getProcessDefinitionById(processInstance.getProcessDefinitionId());
			
			
			int activeInstancesCount=RunnerSingleton.getInstance().getThreadCountByGroup(processDefinition.getCode());
			
			int remaining=processDefinition.getMaxThreadCount()-activeInstancesCount;
			
			if (remaining<=0) {
				break;
			}
			

			boolean isAlreadyRunning = RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode(), processDefinition.getCode());
			
			if (isAlreadyRunning) {
				continue;
			}
			
			
			Thread th=new Thread(new ThreadForInstanceRunner(processService, processInstance));
			th.start();
			HelperUtil.sleep(300L);

			QueueSingleton.getInstance().remove(processInstance);
			
			
			int runningProcessCount=RunnerSingleton.getInstance().getRunningProcessCount();

			if (runningProcessCount>=maxProcessCount) {
				break;
			}
		}

		RunnerSingleton.getInstance().stop(threadName);

	}

}
