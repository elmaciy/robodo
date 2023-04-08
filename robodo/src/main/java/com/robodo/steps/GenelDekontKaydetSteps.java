package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class GenelDekontKaydetSteps extends BaseEpatsSteps {

	public GenelDekontKaydetSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		selenium.sleep(5L);

	}

}
