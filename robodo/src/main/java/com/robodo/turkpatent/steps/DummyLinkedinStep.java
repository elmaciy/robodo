package com.robodo.turkpatent.steps;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.base.BaseWebStep;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class DummyLinkedinStep extends BaseWebStep {

	public DummyLinkedinStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
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
		selenium.navigate("https://www.linkedin.com");
		selenium.sendKeys(selenium.getWebDriver().findElement(By.id("session_key")), "elmaciy@hotmail.com");
		selenium.sendKeys(selenium.getWebDriver().findElement(By.id("session_password")), "Zey!1323");
		selenium.click(selenium.getWebDriver().findElement(By.cssSelector("button[type=submit]")));
		String keyword=getVariable("keyword");
		
		selenium.sendKeys(selenium.getWebDriver().findElement(By.cssSelector(".search-global-typeahead__input")), keyword);
		selenium.enter();
		
		List<WebElement> searchResults = selenium.getWebDriver().findElements(By.cssSelector("a.app-aware-link span.entity-result__title-text a[href*=miniProfileUrn]"));
		for (WebElement elResLnk : searchResults) {
			elResLnk.click();
		}
		selenium.sleep(30L);
		
	}

	

}
