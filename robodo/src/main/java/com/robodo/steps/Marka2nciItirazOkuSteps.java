package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class Marka2nciItirazOkuSteps extends BaseEpatsSteps {

	public Marka2nciItirazOkuSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		//selenium.startWebDriver();
		selenium.stopDriver();

	}

	

}
