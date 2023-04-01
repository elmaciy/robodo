package com.robodo.pages;

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


	public PageEpatsTahakkukOde(SeleniumUtil selenium) {
		super(selenium);
	}
	
	public String getOdemeTutqri() {
		String msg=elPaymentAlert.getText();
		int pos=msg.indexOf("ödemeniz için");
		if (pos==-1) {
			throw new RuntimeException("ödenecek tutar ekranda bulunamadı. %s".formatted(msg));
		}
		String tutar=msg.substring(0, pos-1);
		return tutar;
	}
	
	public void kartBilgileriniGir(String kartNo, String sonKullanma, String cvv) {
		
		selenium.setValue(elKartNo, kartNo);
		selenium.setValue(elKartExpire, sonKullanma);
		selenium.setValue(elKartCVV, cvv);
		
		
	}
	
	public String getDekontNo() {
		return String.valueOf(System.currentTimeMillis());
	}

	public void odemeYap() {
		//selenium.click(btOde);
		
	}

}
