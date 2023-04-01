package com.robodo.threads;

import com.robodo.ui.UIProcessor;

public class ThreadForUIUpdating  implements Runnable{
	UIProcessor uiProcessor;
	
	public ThreadForUIUpdating(UIProcessor uiProcessor) {
		this.uiProcessor=uiProcessor;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
