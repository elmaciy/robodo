package com.robodo.base;

import org.openqa.selenium.support.PageFactory;

import com.robodo.utils.SeleniumUtil;

public class BasePage {
	public SeleniumUtil selenium;
	public BasePage(SeleniumUtil selenium) {
		this.selenium=selenium;
		PageFactory.initElements(this.selenium.getWebDriver(), this);
	}
	
	

}
