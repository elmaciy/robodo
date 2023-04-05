package com.robodo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsOnIzleme extends PageEpatsBase {

	public PageEpatsOnIzleme(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath = "//div[text()='İşlemi Tamamla ve Ödeme Adımına Geç >']")
	WebElement btTahakkukOlustur;
	
	private String extractValueByCaption(String caption) {
		return selenium.getWebDriver().findElement(By.xpath("//td[b[text()='%s']]//following-sibling::td".formatted(caption))).getText();
	}
	
	
	public String getDosyaNumarasi() {
		return extractValueByCaption("Başvuru Numarası");
	}
	
	public String getRefeansTakipNumarasi() {
		return extractValueByCaption("Referans Numarası");
	}
	
	public String getBulusBasligi() {
		return extractValueByCaption("Buluş Başlığı");
	}
	
	public String getMarkaAdi() {
		return extractValueByCaption(" Marka Adı ");
	}
	
	public String getFaturaKimlikNNumarasi(String title) {
		return extractValueByCaption(title);
	}
	
	public String getCezaTutari() {
		return extractValueByCaption("CEZA TUTARI");
	}
	
	public String getGenelToplamTutari() {
		return extractValueByCaption("GENEL TOPLAM");
	}
	
	public void tahakkukOlustur() {
		selenium.click(btTahakkukOlustur);
	}
	
	
	

}
