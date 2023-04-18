package com.robodo.turkpatent.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.base.BaseWebStep;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyBingSearchByKeywordStep extends BaseWebStep {

	public DummyBingSearchByKeywordStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
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
		selenium.navigate("https://www.bing.com/");
		WebElement findElement = selenium.getWebDriver().findElement(By.cssSelector("#sb_form_q"));
		String keyword=getVariable("keyword");
		selenium.click(findElement);
		selenium.sendKeys(findElement, keyword);
		selenium.enter();
		selenium.waitPageLoaded();
		takeStepScreenShot(this.processInstanceStep, keyword, true);		
		
		setVariable("bing.param", "set");

		
	}
	


	

}
