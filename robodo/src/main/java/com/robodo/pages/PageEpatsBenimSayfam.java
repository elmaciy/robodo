package com.robodo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsBenimSayfam extends BasePage {
	
	private static final String ROOT_XPATH_DOSYALARIM="//div[contains(text(),'Dosyalarım')]/../../../../..";
	private static final String ROOT_XPATH_GRID_DOSYALAR="//div[@id='datagrid360']";
	
	public PageEpatsMenu menu;
	
	
	@FindBy(css="input[placeholder='Başvurularınız arasında arama yapın'], input[placeholder='Dosya numarasından veya sahip bilgisinden arama yapınız...']")
	WebElement elArama;
	@FindBy(xpath=ROOT_XPATH_DOSYALARIM+"//i[@class='fa fa-search']/..")
	WebElement elBtnAra;

	@FindBy(xpath=ROOT_XPATH_GRID_DOSYALAR+"//span[text()='Başvuru Türü']/../..//input")
	WebElement inputDosyaTuru;
	
	@FindBy(css="#selectbox218 div.form-item-control")
	WebElement elIslemCombo;
	
	@FindBy(css="#selectbox218 input[type=search]")
	WebElement elIslemComboSearchInput;
	
	@FindBy(xpath=ROOT_XPATH_DOSYALARIM+"//div[@class='btn btn-default' and text()='Git']")
	WebElement elIslemeGit;
	
	
	

	public PageEpatsBenimSayfam(SeleniumUtil selenium) {
		super(selenium);
	}



	public void dosyaArama(String dosyaNo, String basvuruTuru) {
		selenium.setValue(elArama, dosyaNo);
		selenium.click(elBtnAra);
		selenium.setValue(inputDosyaTuru, basvuruTuru);
		List<WebElement> rows = selenium.getWebDriver().findElements(By.cssSelector("div#datagrid360  div.ui-grid-canvas:has(div.ui-grid-cell-contents)"));
		if (rows.size()==0) {
			throw new RuntimeException("Dosya Bulunamadı : %s/%s".formatted(dosyaNo, basvuruTuru));
		}
		selenium.click(rows.get(0));
	}



	public void islemSec(String islemAdi) {
		selenium.click(elIslemCombo);
		selenium.setValue(elIslemComboSearchInput, islemAdi);
		selenium.tab();
		selenium.click(elIslemeGit);
		
	}
}
