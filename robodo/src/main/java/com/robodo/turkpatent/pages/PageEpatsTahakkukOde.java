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
	
	@FindBy(xpath = "//div[contains(text(),'Provizyon Numaras')]")
	WebElement lblOdemeOnayProvizyon;
	
	@FindBy(xpath = "//h2[text()='Ödemeniz tahsil edilmiştir.']")
	WebElement lblOdemeBasarilidir;
	
	@FindBy(xpath = "//button[@class='swal2-confirm swal2-styled']")
	WebElement btnOdemeBasariliNotifikasyonKapat;
	
	
	
	public PageEpatsTahakkukOde(SeleniumUtil selenium) {
		super(selenium);
	}
	
	public void kartBilgileriniGir(String kartNo, String sonKullanma, String cvv) {
		selenium.setValue(elKartNo, kartNo);
		selenium.setValue(elKartExpire, sonKullanma);
		selenium.setValue(elKartCVV, cvv);
	}
	
	public String getDekontNo() {
		try {
			selenium.switchToMainFrame();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String onayIcerik = lblOdemeOnayProvizyon.getText();
		String dekontNo = StringUtils.substringAfter(onayIcerik, "Provizyon Numarası : ");
		if (dekontNo.contains(" ")) {
			dekontNo=StringUtils.substringBefore(dekontNo, " ");
		}
		return dekontNo.strip();
	}
	
	

	public void odemeYap() {
		selenium.click(btOde);
		selenium.sleep(10L);
	}

	public void odemeVazgec() {
		selenium.switchToMainFrame();
		selenium.click(btClose);
		selenium.sleep(3L);
		var closeBtn=selenium.getWebDriver().findElement(By.xpath("//h2[text()='Ödemeniz tahsil edilememiştir.']/../..//button[text()='Tamam']"));
		selenium.click(closeBtn);		
	}

	public void odemeBasariliNotifikasyonKontrolveKapat() {
		selenium.switchToMainFrame();
		//String odemeMesaji = lblOdemeBasarilidir.getText();
		//if (!odemeMesaji.contains("Ödemeniz tahsil edilmiştir")) {
		//	throw new RuntimeException("Tahsilat Basarisiz. Kontrol ediniz.");
		//}
		try {
			String odemeMesaji = lblOdemeBasarilidir.getText();
			selenium.logger("Ödeme mesajı : %s".formatted(odemeMesaji));
			selenium.click(btnOdemeBasariliNotifikasyonKapat);
		} catch(Exception e) {
			throw new RuntimeException("Tahsilat Basarisiz. Kontrol ediniz.");
		}
		
		
	}

}
