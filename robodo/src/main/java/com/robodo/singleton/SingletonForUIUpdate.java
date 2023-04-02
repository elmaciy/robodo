package com.robodo.singleton;

public class SingletonForUIUpdate {
	
	static long lastUpdate=0;
	private static SingletonForUIUpdate instance;
	
	private SingletonForUIUpdate() {
		
	}
	
	public static SingletonForUIUpdate getInstance() {
		if (instance==null) {
			instance=new SingletonForUIUpdate();
		}
		return instance;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate() {
		SingletonForUIUpdate.lastUpdate = System.currentTimeMillis();
	}
	
	
	
	

}