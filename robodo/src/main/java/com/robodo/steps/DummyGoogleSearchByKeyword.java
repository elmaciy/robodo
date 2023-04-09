package com.robodo.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyGoogleSearchByKeyword extends BaseSteps {

	public DummyGoogleSearchByKeyword(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
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
		selenium.navigate("http://www.google.com");
		WebElement findElement = selenium.getWebDriver().findElement(By.cssSelector("[name=q]"));
		String keyword=getVariable("keyword");
		selenium.setValue(findElement, keyword);
		selenium.enter();
		takeStepScreenShot(this.processInstanceStep, "arama sonucu", true);
		
	}
	
	

	

}
