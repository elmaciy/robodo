package com.robodo.turkpatent.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsItirazaIliskinBilgiler extends PageEpatsBase {

	public PageEpatsItirazaIliskinBilgiler(SeleniumUtil selenium) {
		super(selenium);
	}
	
	//@FindBy(css="div.upload-zone")
	@FindBy(xpath="//input[@type='file']")
	WebElement elItirazaIliskinEvrak;

	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;
	
	public void itirazaIliskinEvrakYukle(String filename) {
		uploadFileBySingleFileZone(elItirazaIliskinEvrak, filename);
	}

	public void devamEt() {
		selenium.waitElementClickable(btDevam);
		selenium.scrollToElement(btDevam);
		waitProcessorGone();
		selenium.click(btDevam);
	}

	
	
	

}
