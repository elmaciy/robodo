package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsDosyaBilgisi extends PageEpatsBase {

	public PageEpatsDosyaBilgisi(SeleniumUtil selenium) {
		super(selenium);
	}
	
	@FindBy(xpath="//div[text()='*Başvuru Numarası']/..//input") 
	WebElement elEditBasvuruNumarasi;
	
	@FindBy(css="div.btn.btn-default:has(i.fa.fa-search)")
	WebElement elBtnBascuruNumarasiAra;
	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;
	
	
	@FindBy(xpath = "//div[text()='Başvuru Numarası']/../div/div/div")
	WebElement elBasvuruNumarasi;
	
	@FindBy(xpath = "//div[text()='Başvuru Tarihi']/../div/div/div")
	WebElement elBasvuruTarihi;

	@FindBy(xpath = "//div[text()='Buluş Başlığı']/../div/div/div")
	WebElement elBulusBasligi;
	
	@FindBy(xpath = "//div[text()='Marka Adı']/../div/div/div")
	WebElement elMarkaAdi;
	
	
	
	
	public String getBasvuruNumarasi() {
		return elBasvuruNumarasi.getText();
	}
	
	public String getBasvuruTarihi() {
		return elBasvuruTarihi.getText();
	}
	
	public String getBulusBasligi() {
		return elBulusBasligi.getText();
	}
	
	public String getmarkaAdi() {
		return elMarkaAdi.getText();
	}
	
	
	public void basvuruNumarasiAra(String basvuruNumarasi) {
		selenium.setValue(elEditBasvuruNumarasi, basvuruNumarasi);
		selenium.click(elBtnBascuruNumarasiAra);
	}
	
	public void devamEt() {
		selenium.click(btDevam);
	}
	
	
	

}
