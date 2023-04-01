package com.robodo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.robodo.threads.ThreadForDiscoverers;
import com.robodo.threads.ThreadForInstanceStarter;

@Component
public class ScheduledTasks {
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	Environment env;
		
	@Scheduled(fixedRate = 120000, initialDelay = 0)
	public void runDiscoverers() {
		Thread th=new Thread(new ThreadForDiscoverers(processService, env));
		th.start();
	}
	
	
	@Scheduled(fixedRate = 30000, initialDelay = 10000)
	public void runSteps() {
		Thread th=new Thread(new ThreadForInstanceStarter(processService, env));
		th.start();
	}

}
