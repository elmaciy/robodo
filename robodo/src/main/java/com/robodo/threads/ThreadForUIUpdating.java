package com.robodo.threads;

import static org.hamcrest.CoreMatchers.is;

import com.robodo.singleton.SingletonForUIUpdate;
import com.robodo.ui.UIProcessor;

public class ThreadForUIUpdating  implements Runnable{
	UIProcessor uiProcessor;
	long lastUpdate=0;
	
	public ThreadForUIUpdating(UIProcessor uiProcessor) {
		this.uiProcessor=uiProcessor;
	}

	@Override
	public void run() {
		while(true) {
			try {
				//bu sleep degeri ekrandan alinabilir. 
				Thread.sleep(uiProcessor.getRefreshInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("thread is refreshing...");
			long ts = SingletonForUIUpdate.getInstance().getLastUpdate();
			if (ts==0) continue;
			if (ts>lastUpdate) {
				lastUpdate=ts;
				try {
					boolean isRefreshed = this.uiProcessor.refreshProcessDefinitionGrid();
					if (!isRefreshed) {
						System.out.println("UI is detached. exit updater thread. ");
					}
				} catch(Exception e) {
					System.out.println("UI is detached. exit updater thread. ");
					e.printStackTrace();
					break;
				}
				
			}
			
		}
		
	}
	

}
