package com.robodo.steps;

import com.robodo.utils.RunnerUtil;

public class GenelDekontKaydetSteps extends BaseEpatsSteps {

	public GenelDekontKaydetSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
	}

	@Override
	public void run() {
		selenium.startWebDriver();
		selenium.sleep(5L);
		selenium.stopDriver();

	}

}
