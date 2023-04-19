package com.robodo.turkpatent.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsTahakkukOde extends PageEpatsBase {
		
	public PageEpatsMenu menu;
	
	@FindBy(css="#paymentAlert")
	WebElement elPaymentAlert;
	
	@FindBy(css="#pf_pan")
	WebElement elKartNo;
	
	@FindBy(css="#pf_expires")
	WebElement elKartExpire;
	
	@FindBy(css="#pf_cv2")
	WebElement elKartCVV;
	
	@FindBy(css="#confirm")
	WebElement btOde;
	
	@FindBy(css="div.btn.btn-default i.fa.fa-times")
	WebElement btClose;


	public PageEpatsTahakkukOde(SeleniumUtil selenium) {
		super(selenium);
	}
	
	public void kartBilgileriniGir(String kartNo, String sonKullanma, String cvv) {
		
		
		selenium.setValue(elKartNo, kartNo);
		selenium.setValue(elKartExpire, sonKullanma);
		selenium.setValue(elKartCVV, cvv);
		
		
	}
	
	public String getDekontNo() {
		//todo : buraya gerçek kod eklenecek. 
		return "DEK"+String.valueOf(System.currentTimeMillis());
	}
	
	private void close() {
		selenium.switchToMainFrame();
		selenium.click(btClose);
		selenium.sleep(3L);
		var closeBtn=selenium.getWebDriver().findElement(By.xpath("//h2[text()='Ödemeniz tahsil edilememiştir.']/../..//button[text()='Tamam']"));
		selenium.click(closeBtn);
	}

	public void odemeYap() {
		//selenium.click(btOde);
		close();
	}

}
