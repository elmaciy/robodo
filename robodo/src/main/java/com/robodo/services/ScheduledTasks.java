package com.robodo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.robodo.threads.ThreadForDiscoverers;
import com.robodo.threads.ThreadForInstanceStarter;
import com.robodo.threads.ThreadForQueueManager;
import com.robodo.threads.ThreadForRetryFailed;
import com.robodo.threads.ThreadForExpiredTokenRemoval;

@Component
public class ScheduledTasks {
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	Environment env;
		
	@Scheduled(fixedRate = 30000, initialDelay = 0)
	public void runDiscoverers() {
		Thread th=new Thread(new ThreadForDiscoverers(processService));
		th.start();
	}

	@Scheduled(fixedRate = 10000, initialDelay = 0)
	public void manageQueue() {
		Thread th=new Thread(new ThreadForQueueManager(processService));
		th.start();
	}

	
	@Scheduled(fixedRate = 10000, initialDelay = 0)
	public void runProcessInstances() {
		Thread th=new Thread(new ThreadForInstanceStarter(processService));
		th.start();
	}

	@Scheduled(fixedRate = 10000, initialDelay = 0)
	public void retryFailedInstances() {
		Thread th=new Thread(new ThreadForRetryFailed(processService));
		th.start();
	}
	
	
	@Scheduled(fixedRate = 30*1000, initialDelay = 0)
	public void removeExpiredTokens() {
		Thread th=new Thread(new ThreadForExpiredTokenRemoval(processService));
		th.start();
	}
}
