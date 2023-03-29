package com.robodo.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.robodo.threads.ThreadForDiscoverers;
import com.robodo.threads.ThreadForSteps;

@Component
public class ScheduledTasks {
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	Environment env;
		
	@Scheduled(fixedRate = 60000, initialDelay = 10000)
	public void runDiscoverers() {
		Thread th=new Thread(new ThreadForDiscoverers(processService, env));
		th.start();
	}
	
	
	@Scheduled(fixedRate = 30000, initialDelay = 10000)
	public void runSteps() {
		Thread th=new Thread(new ThreadForSteps(processService, env));
		th.start();
	}

}
