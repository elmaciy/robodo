package com.robodo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsItirazGerekceleri extends PageEpatsBase {

	public PageEpatsItirazGerekceleri(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath="//div[text()='*Dosya No veya Marka Adı']/..//input")
	WebElement elEditDosyaAra;
	

	@FindBy(css="div.btn.btn-default:has(i.fa.fa-search)")
	WebElement elBtnAra;
	
	@FindBy(css="div.btn.btn-default:has(i.fa.fa-plus)")
	WebElement elBtnEkle;
	
	
	
	@FindBy(xpath = "//div[text()='Devam Et  >']")
	WebElement btDevam;

	public void itirazGerekcesiIsaretle(String gerekce) {
		String xpath="//div[text()='%s']/..//input".formatted(gerekce);
		WebElement elGerekceSecim = selenium.getWebDriver().findElement(By.xpath(xpath));
		selenium.click(elGerekceSecim);
	}
	
	public void devamEt() {
		selenium.waitElementClickable(btDevam);
		selenium.scrollToElement(btDevam);
		selenium.click(btDevam);
	}

	public void itirazaGerekceDosyaEkle(String dosya) {
		selenium.scrollToElement(elEditDosyaAra);
		selenium.setValue(elEditDosyaAra, dosya);
		selenium.click(elBtnAra);
		selectGridFirstRow("İtiraz Sahibi Dosya Listesi");
		selenium.scrollToElement(elBtnEkle);
		selenium.click(elBtnEkle);
		
	}
	
	
	

}
