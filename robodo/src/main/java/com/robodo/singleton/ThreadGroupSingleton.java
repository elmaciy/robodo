package com.robodo.singleton;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

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
			counter+=hmThreadGroups.get(it.next()).activeCount();
		}
		return counter;
	}

}
