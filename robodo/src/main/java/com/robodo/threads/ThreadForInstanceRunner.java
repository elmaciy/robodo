package com.robodo.threads;

import java.util.List;

import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.runner.RunnerUtil;
import com.robodo.services.ProcessService;
import com.robodo.singleton.ThreadGroupSingleton;

public class ThreadForInstanceRunner implements Runnable {
	
	private ProcessService processService;
	Environment env;
	ProcessInstance processInstance;
	
	public ThreadForInstanceRunner(ProcessService processService, Environment env, ProcessInstance processInstance) {
		this.processService=processService;
		this.env=env;
		this.processInstance=processInstance;
	}

	@Override
	public void run() {
		
		RunnerUtil runner=new RunnerUtil(processService, env);
		System.err.println("running task : %s".formatted(processInstance.getCode()));
		ProcessInstance processInstanceAfterRun = runner.runProcessInstance(processInstance);
		if (processInstanceAfterRun == null) {
			System.err.println("exception at task : %s/%s".formatted(processInstance.getCode(),processInstance.getProcessDefinition().getCode()));
			return;
		}
		
		processService.saveProcessInstance(processInstanceAfterRun);

	}

}
