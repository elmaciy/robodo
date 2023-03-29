package com.robodo.steps;

import com.robodo.runner.RunnerUtil;

public class DekontKaydetSteps extends BaseSteps {

	public DekontKaydetSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
	}

	@Override
	public void run() {
		selenium.startWebDriver();
		selenium.sleep(5L);
		selenium.stopDriver();

	}

}
