package com.robodo.singleton;

import java.util.HashMap;
import java.util.Hashtable;

public class RunnerSingleton {
	
	private static RunnerSingleton instance;
	
	private Hashtable<String,Long> htRunningInstances=new Hashtable<String,Long>();
	public static final long TIMEOUT=5*60*1000;
	
	private RunnerSingleton() {
		
	}
	
	public static RunnerSingleton getInstance() {
		if (instance==null) {
			instance = new RunnerSingleton();
		}
		
		return instance;
	}
	
	public boolean hasRunningInstance(String runId) {
		boolean isExists =  htRunningInstances.containsKey(runId);
		if (!isExists) {
			return false;
		}
		
		long startTime=htRunningInstances.get(runId);
		if (System.currentTimeMillis()-startTime> TIMEOUT) {
			stop(runId);
			return false;
		}
		return true;
	}
	
	public void start(String runId) {
		htRunningInstances.put(runId, System.currentTimeMillis());
	}
	
	public void stop(String runId) {
		if (htRunningInstances.containsKey(runId)) {
			htRunningInstances.remove(runId);
		}
	}

}
