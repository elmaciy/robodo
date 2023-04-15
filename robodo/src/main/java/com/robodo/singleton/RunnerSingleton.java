package com.robodo.singleton;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.robodo.model.KeyValue;
import com.robodo.model.RunningProcess;

public class RunnerSingleton {
	
	private static RunnerSingleton instance;
	
	private ConcurrentHashMap<RunningProcess,Long> hmRunningInstances=new ConcurrentHashMap<RunningProcess,Long>();
	public static final long TIMEOUT=5*60*1000;
	
	private RunnerSingleton() {
		
	}
	
	public static RunnerSingleton getInstance() {
		if (instance==null) {
			instance = new RunnerSingleton();
		}
		
		return instance;
	}
	
	
	private synchronized  boolean syncaction(String name, String group, String action, Long startTs) {
		var rp = new RunningProcess(name, group, startTs);
		if (action.equals("PUT")) {
			hmRunningInstances.put(rp, startTs);
			return true;
		} else if (action.equals("GET")) {
			return hmRunningInstances.containsKey(rp);
		} else if (action.equals("REMOVE")) {
			if (hmRunningInstances.containsKey(rp)) {
				hmRunningInstances.remove(rp);
			}
			return true;
		}
		
		return true;
		
	}
	
	public boolean hasRunningInstance(String name, String group) {		
		boolean isExists =  syncaction(name, group, "GET", 0L);
		if (!isExists) {
			return false;
		}
	
		Long startTime = hmRunningInstances.get(new RunningProcess(name, group, 0));
		if (System.currentTimeMillis()-startTime> TIMEOUT) {
			stop(name, group);
			return false;
		}
		
		
		return true;
	}
	
	public boolean hasRunningInstance(String name) {
		return hasRunningInstance(name, name);
	}
	
	public void start(String name, String group) {
		syncaction(name, group, "PUT", System.currentTimeMillis());
	}
	
	public void stop(String name, String group) {
		syncaction(name, group, "REMOVE", 0L);
	}
	
	public void start(String name) {
		start(name,name);
	}
	
	public void stop(String name) {
		stop(name,name);
	}

	public List<RunningProcess> getProcesses() {
		return hmRunningInstances.keySet().stream().collect(Collectors.toList());
	}

	public List<KeyValue> getThreadGroupsAsKeyValue() {
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		List<RunningProcess> processes = getProcesses();
		processes.forEach(p->{
			if (!map.containsKey(p.getGroup())) {
				map.put(p.getGroup(), Integer.valueOf(0));
			}
			
			Integer currentCount = map.get(p.getGroup());
			map.put(p.getGroup(), currentCount+1);
		});
		
		return map.entrySet().stream().map(e->{
			return new KeyValue(e.getKey(), String.valueOf(e.getValue()));
		}).collect(Collectors.toList());
	}

	public int getRunningProcessCount() {
		return (int) getProcesses().stream().filter(p-> !p.getName().equals(p.getGroup())).count();
	}

	
	public int getThreadCountByGroup(String group) {
		return (int) getProcesses().stream().filter(p->p.getGroup().equals(group)).count();
	}


}
