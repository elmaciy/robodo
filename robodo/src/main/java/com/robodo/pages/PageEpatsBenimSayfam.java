package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.runner.SeleniumUtil;

public class PageEpatsBenimSayfam extends BasePage {
	
	public PageEpatsMenu menu;
	
	@FindBy(css="[placeholder='Başvurularınız arasında arama yapın']")
	WebElement elArama;
	@FindBy(css="i.fa-search")
	WebElement elBtnAra;

	

	public PageEpatsBenimSayfam(SeleniumUtil selenium) {
		super(selenium);
	}



	public void basvuruArama(String basvuru) {
		selenium.setValue(elArama, basvuru);
		selenium.click(elBtnAra);
		
	}
}
