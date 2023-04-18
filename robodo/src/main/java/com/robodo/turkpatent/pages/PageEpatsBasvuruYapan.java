package com.robodo.turkpatent.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsBasvuruYapan extends PageEpatsBase {

	public PageEpatsBasvuruYapan(SeleniumUtil selenium) {
		super(selenium);
	}
	
	@FindBy(css="input[type=email]")
	WebElement elBasvuruEmail;
	
	
	@FindBy(css="input[ui-mask='(999) 999 9999']")
	WebElement elBasvuruCepTelefonu;
	
	@FindBy(xpath="//div[text()='Referans Numarası']/../../../../..//input")
	WebElement elReferansNo;
	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;
	
	public void basvuruBilgileriniDoldur(String eposta, String cepTel, String refNo) {
		selenium.setValue(elBasvuruEmail, eposta);
		selenium.setValue(elBasvuruCepTelefonu, cepTel);
		selenium.setValue(elReferansNo, refNo);
	}
	
	public void devamEt() {
		selenium.click(btDevam);
	}
	
	
	

}
