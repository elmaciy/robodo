package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public class PageEpatsHome extends BasePage {
	
	@FindBy(xpath = "//a")
	WebElement elGirisEdevlet;

	public PageEpatsHome(SeleniumUtil selenium) {
		super(selenium);
	}

	public void clickEdevlet() {
		selenium.click(elGirisEdevlet);
		
	}

	public void open() {
		selenium.navigate("https://epats.turkpatent.gov.tr/");
		
	}

}
