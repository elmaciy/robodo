package com.robodo.threads;

import java.util.List;

import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.Tokenization;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;

public class ThreadForExpiredTokenRemoval implements Runnable {
	
	private ProcessService processService;
	
	public ThreadForExpiredTokenRemoval(ProcessService processService) {
		this.processService=processService;
	}

	@Override
	public void run() {
		
		String threadName=this.getClass().getName();
		
		if (RunnerSingleton.getInstance().hasRunningInstance(threadName)) {
			return;
		}
		
		RunnerSingleton.getInstance().start(threadName);

		List<Tokenization> tokensToRemove = processService.getTokensToRemove();
		//System.err.println(tokensToRemove.size()==0 ? "no expired token to remove" : "%d expired tokens to remove".formatted(tokensToRemove.size()));
		for (Tokenization token : tokensToRemove) {
			System.err.println("removing [%s (%s)] token : %s".formatted(token.getPurpose(), token.getPurposeDetail(), token.getToken()));
			processService.removeToken(token);
		}

		
		RunnerSingleton.getInstance().stop(threadName);

	}

}
