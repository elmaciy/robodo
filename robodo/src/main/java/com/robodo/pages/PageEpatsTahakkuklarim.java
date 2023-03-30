package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.runner.SeleniumUtil;

public class PageEpatsTahakkuklarim extends BasePage {
		
	public PageEpatsMenu menu;
	
	
	@FindBy(xpath="//div[@title='Tahakkuk No']/..//input")
	WebElement elFilterTahakkukNo;


	

	public PageEpatsTahakkuklarim(SeleniumUtil selenium) {
		super(selenium);
	}



	public void tahakkukNoArama(String tahakkukNo) {
		selenium.setValue(elFilterTahakkukNo, tahakkukNo);
		selenium.enter();
		
	}
}
