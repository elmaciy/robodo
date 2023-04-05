package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class Marka2nciItirazOlusturSteps extends BaseEpatsSteps {


	
	public Marka2nciItirazOlusturSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();
		islemSec();
		
		selenium.stopDriver();
	}


	


	



	

}
