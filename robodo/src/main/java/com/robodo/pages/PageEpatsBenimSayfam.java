package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.runner.SeleniumUtil;

public class PageEpatsBenimSayfam extends BasePage {
	
	private static final String ROOT_XPATH_DOSYALARIM="//div[text()='Dosyalarım']/../../../../..";
	
	public PageEpatsMenu menu;
	
	
	@FindBy(css="input[placeholder='Başvurularınız arasında arama yapın']")
	WebElement elArama;
	@FindBy(xpath=ROOT_XPATH_DOSYALARIM+"//i[@class='fa fa-search']/..")
	WebElement elBtnAra;

	

	public PageEpatsBenimSayfam(SeleniumUtil selenium) {
		super(selenium);
	}



	public void dosyaArama(String basvuru) {
		selenium.setValue(elArama, basvuru);
		selenium.click(elBtnAra);
		
	}
}
