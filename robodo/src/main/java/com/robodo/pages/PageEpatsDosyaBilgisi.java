package com.robodo.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsDosyaBilgisi extends PageEpatsBase {

	public PageEpatsDosyaBilgisi(SeleniumUtil selenium) {
		super(selenium);
	}
	
	@FindBy(xpath = "//div[text()='Başvuru Numarası']/../div/div/div")
	WebElement elBasvuruNumarasi;
	
	@FindBy(xpath = "//div[text()='Başvuru Tarihi']/../div/div/div")
	WebElement elBasvuruTarihi;

	@FindBy(xpath = "//div[text()='Buluş Başlığı']/../div/div/div")
	WebElement elBulusBasligi;
	

	
	@FindBy(xpath="//bmm-table//tbody/tr//span")
	List<WebElement> elSahipBilgileri;
	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;
	
	public String getBasvuruNumarasi() {
		return elBasvuruNumarasi.getText();
	}
	
	public String getBasvuruTarihi() {
		return elBasvuruTarihi.getText();
	}
	
	public String getBulusBasligi() {
		return elBulusBasligi.getText();
	}
	
	public String getSahipKimlikVergiNo() {
		return elSahipBilgileri.get(0).getText();
	}
	
	public String getSahipUnvan() {
		return elSahipBilgileri.get(1).getText();
	}
	
	
	
	public void devamEt() {
		selenium.click(btDevam);
	}
	
	
	

}
