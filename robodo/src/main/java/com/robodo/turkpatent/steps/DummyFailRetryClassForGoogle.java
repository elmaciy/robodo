package com.robodo.turkpatent.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.base.BaseWebStep;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyFailRetryClassForGoogle extends BaseWebStep {

	public DummyFailRetryClassForGoogle(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {
		selenium.startWebDriver();
		
	}

	@Override
	public void teardown() {
		selenium.stopWebDriver();
		
	}
	
	@Override
	public void run() {
		selenium.navigate("http://www.youtube.com");
		takeStepScreenShot(processInstanceStep, "retry fail screenshot", false);
		
	}
	
	

	

}
