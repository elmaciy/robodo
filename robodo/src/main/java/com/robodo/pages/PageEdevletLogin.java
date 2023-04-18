package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.base.BasePage;
import com.robodo.utils.SeleniumUtil;

public class PageEdevletLogin extends BasePage {
	
	@FindBy(css = "div.tabChooser a span.password")
	WebElement elEdevletTab;
	
	@FindBy(css="input#tridField")
	WebElement elTckimlikno;
	
	@FindBy(css="input#egpField")
	WebElement elSifre;
	
	@FindBy(css="input.submitButton")
	WebElement elSubmit;
	

	

	public PageEdevletLogin(SeleniumUtil selenium) {
		super(selenium);
	}

	public void girisEdevlet(String tckn, String sifre) {
		selenium.click(elEdevletTab);
		selenium.setValue(elTckimlikno, tckn);
		selenium.setValue(elSifre, sifre);
		selenium.click(elSubmit);
	}

	public void open() {
		selenium.navigate("https://epats.turkpatent.gov.tr/");
		
	}

}
