package com.robodo.turkpatent.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsBenimSayfam extends PageEpatsBase {
	
	private static final String ROOT_XPATH_DOSYALARIM="//div[contains(text(),'Dosyalarım')]/../../../../..";
	
	public PageEpatsMenu menu;
	
	
	@FindBy(css="input[placeholder='Başvurularınız arasında arama yapın'], input[placeholder='Dosya numarasından veya sahip bilgisinden arama yapınız...']")
	WebElement elArama;
	@FindBy(xpath=ROOT_XPATH_DOSYALARIM+"//i[@class='fa fa-search']/..")
	WebElement elBtnAra;
	
	
	
	

	public PageEpatsBenimSayfam(SeleniumUtil selenium) {
		super(selenium);
	}

	


	public void dosyaArama(String dosyaNo, String basvuruTuru) {
		selenium.setValue(elArama, dosyaNo);
		selenium.click(elBtnAra);
		setGridFilterByTitle("Dosyalarım (Vekil)", "Başvuru Türü", basvuruTuru);
		selectGridRowByUniqueContent("Dosyalarım (Vekil)", dosyaNo);
	}


	public void islemSec(String islemGrubu, String islemAdi) {
		String xpath="//*[text()='%s']/../../../../..//div[@class='form-item-control' and contains(@ng-if,'selectbox')]".formatted(islemGrubu);
		WebElement elComboIslem=selenium.getWebDriver().findElement(By.xpath(xpath));
		selenium.scrollToElement(elComboIslem);
		setComboboxByTitleContains(elComboIslem, islemAdi);
		xpath="//div[contains(text(),'%s')]/../../../../../..//div[@class='btn btn-default' and text()='Git']".formatted(islemGrubu);
		WebElement elIslemeGit=selenium.getWebDriver().findElement(By.xpath(xpath));
		selenium.click(elIslemeGit);		
	}
}
