package com.robodo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsTalepTuru extends PageEpatsBase {

	public PageEpatsTalepTuru(SeleniumUtil selenium) {
		super(selenium);
	}


	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;


	public void talepTuruSec(String talepTuru) {
		selenium.click(selenium.getWebDriver().findElement(By.xpath("//span[@class='radio-item'][span[contains(text(),'%s')]]/..//input".formatted(talepTuru))));
	}
	
	
	public void devamEt() {
		selenium.click(btDevam);
	}
	
	
	

}
