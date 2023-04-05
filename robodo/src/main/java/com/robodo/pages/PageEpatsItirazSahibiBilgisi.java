package com.robodo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsItirazSahibiBilgisi extends PageEpatsBase {

	public PageEpatsItirazSahibiBilgisi(SeleniumUtil selenium) {
		super(selenium);
	}


	@FindBy(xpath="//u[text()='İtiraz Sahibi']/..") 
	WebElement elItirazSahibiOlarakEkle;
	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;


	public void itirazSahibiEkle(String itirazSahibiAdi, String itirazSahibiKimlikNo) {
		if (itirazSahibiKimlikNo!=null & !itirazSahibiKimlikNo.isEmpty()) {
			setGridFilterByTitle("Kayıtlı Kişiler", "TC Kimlik No/Vergi No", itirazSahibiKimlikNo);
			selectGridRowByUniqueContent("Kayıtlı Kişiler", itirazSahibiKimlikNo);
		} else {
			setGridFilterByTitle("Kayıtlı Kişiler","Ad Soyad/Unvan", itirazSahibiAdi);
			selectGridFirstRow("Kayıtlı Kişiler");
		}
		
		selenium.click(elItirazSahibiOlarakEkle);
		
		
		
		
	}
	
	
	public void devamEt() {
		selenium.scrollToElement(btDevam);
		selenium.waitElementClickable(btDevam);
		waitProcessorGone(); 
		//selenium.sleep(10L);
		selenium.click(btDevam);
	}


	
	
	
	

}
