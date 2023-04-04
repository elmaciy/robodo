package com.robodo.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyGoogleSearchByKeywordSteps extends BaseSteps {

	public DummyGoogleSearchByKeywordSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		selenium.startWebDriver();
		selenium.navigate("http://www.google.com");
		WebElement findElement = selenium.getWebDriver().findElement(By.cssSelector("[name=q]"));
		String keyword=getVariable("keyword");
		selenium.setValue(findElement, keyword);
		selenium.enter();
		takeStepScreenShot(this.processInstanceStep, keyword);
		selenium.stopDriver();
		
	}

}
