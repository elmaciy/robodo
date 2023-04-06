package com.robodo.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyYoutubeSearchByKeyword extends BaseSteps {

	public DummyYoutubeSearchByKeyword(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		selenium.startWebDriver();
		selenium.navigate("https://www.youtube.com/");
		WebElement findElement = selenium.getWebDriver().findElement(By.cssSelector("ytd-searchbox#search"));
		String keyword=getVariable("keyword");
		selenium.click(findElement);
		//selenium.setValue(findElement, keyword);
		selenium.sendKeys(findElement, keyword);
		selenium.enter();
		takeStepScreenShot(this.processInstanceStep, keyword, true);		
		selenium.stopDriver();
		
	}

}
