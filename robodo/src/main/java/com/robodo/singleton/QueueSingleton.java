package com.robodo.singleton;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstance;

public class QueueSingleton {
	
	 private Queue<ProcessInstance> queue = new LinkedBlockingQueue<ProcessInstance>();
	 private static QueueSingleton instance;

	 private QueueSingleton() {
		 
	 }
	 
	 public static QueueSingleton getInstance() {
		 if (instance==null) {
			 instance=new QueueSingleton();
		 }

		 return instance;
	 }
	 
	 public ProcessInstance get() {
		 if (this.queue.isEmpty()) {
			 return null;
		 }
		 return this.queue.peek();
	 }
	 
	 public void add(ProcessInstance processInstance) {
		System.err.println("+QUEUE : %s".formatted(processInstance.getCode()));
		this.queue.add(processInstance);
	 }
	 
	 public boolean inQueue(ProcessInstance processInstance) {
		 return this.queue.stream().anyMatch(p->p.getCode().equals(processInstance.getCode()));
	 }
	 
	 public List<KeyValue> getAllAsKeyValue() {
		 return this.queue.stream().map(e->new KeyValue(e.getCode(), e.getStatus())).toList();
	 }

	public void remove(ProcessInstance processInstance) {
		System.err.println("-DEQUEUE : %s".formatted(processInstance.getCode()));
		if (this.queue.contains(processInstance)) {
			this.queue.remove(processInstance);
		}
		
	}

	public int getQueueLength() {
		return this.queue.size();
	}

	public void removeByCode(String code) {
		this.queue.removeIf(p->p.getCode().equals(code));
		
	}

}
