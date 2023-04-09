package com.robodo.singleton;

import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import com.robodo.model.KeyValue;

public class RunnerSingleton {
	
	private static RunnerSingleton instance;
	
	private Hashtable<String,Long> hmRunningInstances=new Hashtable<String,Long>();
	public static final long TIMEOUT=5*60*1000;
	
	private RunnerSingleton() {
		
	}
	
	public static RunnerSingleton getInstance() {
		if (instance==null) {
			instance = new RunnerSingleton();
		}
		
		return instance;
	}
	
	
	private synchronized  boolean syncaction(String runId, String action, Long param) {
		if (action.equals("PUT")) {
			hmRunningInstances.put(runId, param);
			return true;
		} else if (action.equals("GET")) {
			return hmRunningInstances.containsKey(runId);
		} else if (action.equals("REMOVE")) {
			if (hmRunningInstances.containsKey(runId)) {
				hmRunningInstances.remove(runId);
			}
			return true;
		}
		
		return true;
		
	}
	
	public boolean hasRunningInstance(String runId) {
		boolean isExists =  syncaction(runId, "GET", null);
		if (!isExists) {
			return false;
		}
		
		long startTime=hmRunningInstances.get(runId);
		if (System.currentTimeMillis()-startTime> TIMEOUT) {
			stop(runId);
			return false;
		}
		
		
		return true;
	}
	
	public void start(String runId) {
		syncaction(runId, "PUT", System.currentTimeMillis());
	}
	
	public void stop(String runId) {
		syncaction(runId, "REMOVE", null);
	}

	public List<KeyValue> getProcesses() {
		return hmRunningInstances.entrySet().stream().map(e->{
			return new KeyValue(e.getKey(), String.valueOf(e.getValue()));
		}).collect(Collectors.toList());
		
	}


}
