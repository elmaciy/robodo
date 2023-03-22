package com.robodo.pages;

import org.openqa.selenium.support.PageFactory;

import com.robodo.runner.RunnerUtil;
import com.robodo.runner.SeleniumUtil;

public class BasePage {
	public SeleniumUtil selenium;
	public BasePage(SeleniumUtil selenium) {
		this.selenium=selenium;
		PageFactory.initElements(this.selenium.getWebDriver(), this);
	}
	
	

}
