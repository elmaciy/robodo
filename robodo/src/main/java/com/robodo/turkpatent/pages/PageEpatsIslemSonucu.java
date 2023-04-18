package com.robodo.turkpatent.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsIslemSonucu extends PageEpatsBase {

	public PageEpatsIslemSonucu(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath = "//*[contains(text(),'Sayın EMİN KORHAN DERİCİOĞLU')]")
	WebElement elSonuc;
	
	@FindBy(xpath = "//div[text()='<  Ana Sayfa']")
	WebElement btAnaSayfayaDon;

	public void anaSayfayaDon() {
		selenium.click(btAnaSayfayaDon);
	}
	
	public String sonucAl() {
		return elSonuc.getText();
	}
	

}
