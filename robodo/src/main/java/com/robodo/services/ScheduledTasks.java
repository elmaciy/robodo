package com.robodo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.robodo.threads.ThreadForDiscoveryStarter;
import com.robodo.threads.ThreadForInstanceStarter;
import com.robodo.threads.ThreadForQueueManager;
import com.robodo.threads.ThreadForRetryFailed;

@Component
public class ScheduledTasks {
	
	@Autowired
	ProcessService processService;
	
	@Scheduled(fixedRate = 30000, initialDelay = 0)
	public void runDiscoverers() {
		Thread th=new Thread(new ThreadForDiscoveryStarter(processService));
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


}
