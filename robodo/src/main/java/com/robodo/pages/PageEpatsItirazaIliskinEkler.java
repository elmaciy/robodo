package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsItirazaIliskinEkler extends PageEpatsBase {

	public PageEpatsItirazaIliskinEkler(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;


	public void devamEt() {
		selenium.waitElementClickable(btDevam);
		selenium.scrollToElement(btDevam);
		waitProcessorGone();
		selenium.click(btDevam);
	}

	
	
	

}
