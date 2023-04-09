package com.robodo.singleton;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.robodo.model.KeyValue;

public class ThreadGroupSingleton {
	private static ThreadGroupSingleton instance;
	ConcurrentHashMap<String, ThreadGroup> hmThreadGroups=new ConcurrentHashMap<String, ThreadGroup>();
	
	private ThreadGroupSingleton() {
		
	}
	
	public static ThreadGroupSingleton getInstance() {
		if (instance==null) {
			instance=new ThreadGroupSingleton();
		}
		
		return instance;
	}


	public ThreadGroup getThreadGroupByName(String name) {
		if (!hmThreadGroups.containsKey(name)) {
			ThreadGroup thGroup=new ThreadGroup(name);
			hmThreadGroups.put(name, thGroup);
		}
	
		return hmThreadGroups.get(name);		
	}
	
	public int getActiveThreadCount() {
		int counter=0;
		Iterator<String> it = hmThreadGroups.keys().asIterator();
		while(it.hasNext()) {
			String key=it.next();
			ThreadGroup tg = hmThreadGroups.get(key);
			counter+= filteredActiveThreadCount(tg);
		}

		return counter;
	}

	public List<KeyValue> getThreadGroupsAsKeyValue() {
		return hmThreadGroups.entrySet().stream().map(e->{
			return new KeyValue(e.getKey(), String.valueOf(filteredActiveThreadCount(e.getValue())));
		}).collect(Collectors.toList());
	}

	public int filteredActiveThreadCount(ThreadGroup tg) {
		Thread[] list=new Thread[tg.activeCount()];
		tg.enumerate(list);
		List<Thread> arr = List.of(list).stream().filter(p->p.getName().equals("Exec Default Executor")).toList();
		return arr.size();
	}

	

}
