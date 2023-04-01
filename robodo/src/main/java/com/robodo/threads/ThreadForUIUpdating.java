package com.robodo.threads;

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
				Thread.sleep(uiProcessor.getRefreshInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String sessionId=uiProcessor.getUI().get().getSession().getSession().getId();
			if (sessionId==null) break;
			//System.out.println("thread is refreshing for session %s".formatted(sessionId));
			long ts = SingletonForUIUpdate.getInstance().getLastUpdate();
			if (ts==0) continue;
			if (ts>lastUpdate) {
				lastUpdate=ts;
				try {
					this.uiProcessor.getUI().get().access(()->{
						boolean isRefreshed = this.uiProcessor.refreshProcessDefinitionGrid();
						if (!isRefreshed) {
							//System.out.println("UI is detached. exit updater thread. ");
							throw new RuntimeException("UI is detached. exit updater thread. ");
						}
					});
					
				} catch(Exception e) {
					System.out.println("UI is detached. exit updater thread. ");
					e.printStackTrace();
					break;
				}
				
			}
			
		}
		
	}
	

}
