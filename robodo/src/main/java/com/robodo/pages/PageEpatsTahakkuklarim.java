package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsTahakkuklarim extends PageEpatsBase {
		
	public PageEpatsMenu menu;
	
	
	@FindBy(xpath="//div[@title='Tahakkuk No']/..//input")
	WebElement elFilterTahakkukNo;

	@FindBy(xpath="//div[text()='Seçili Tahakkuku Öde']")
	WebElement elTahakkukOde;
	

	public PageEpatsTahakkuklarim(SeleniumUtil selenium) {
		super(selenium);
	}



	public void tahakkukNoArama(String tahakkukNo) {
		selenium.setValue(elFilterTahakkukNo, tahakkukNo);
		selenium.enter();
		
	}
	
	public void tahakkukOde() {
		selenium.click(elTahakkukOde);
		
	}
}
