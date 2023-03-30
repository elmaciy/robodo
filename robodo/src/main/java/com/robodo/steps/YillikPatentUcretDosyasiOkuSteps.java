package com.robodo.steps;

import org.openqa.selenium.WebElement;

import com.robodo.runner.RunnerUtil;

public class YillikPatentUcretDosyasiOkuSteps extends BaseEpatsSteps {

	public YillikPatentUcretDosyasiOkuSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
	}

	@Override
	public void run() {
		selenium.startWebDriver();
		selenium.navigate("http://www.google.com");
		WebElement elSearch = selenium.locateElementByCss("[name=q]");
		selenium.setValue(elSearch, "selamlar");
		selenium.pressKey("ENTER");
		selenium.screenShot();
		selenium.stopDriver();

	}

}
