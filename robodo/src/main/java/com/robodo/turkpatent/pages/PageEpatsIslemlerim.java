package com.robodo.turkpatent.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsIslemlerim extends PageEpatsBase {

	public PageEpatsIslemlerim(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath = "//span[text()='Tahakkuk No']/../..//input")
	WebElement elFilterTahakkukNo;
	
	@FindBy(xpath = "//span[text()='Başvuru No']/../..//input")
	WebElement elFilterBasvuruNo;
	
	@FindBy(xpath = "//span[text()='Ödeme Kanalı']/../..//input")
	WebElement elFilterOdemeKanali;
	
	@FindBy(xpath = "//span[text()='Dekont No']/../..//input")
	WebElement elDekontNo;
	
	@FindBy(xpath = "//div[@class='form-item ']//div[@role='row']//div[@class='ui-grid-cell-contents']")
	public List<WebElement> cellContents;
	
	@FindBy(xpath="//i[@class='fa fa-download']/..")
	WebElement lnkDownload;


	public void islemAra(String tahakkukNo, String dosyaNo) {
		selenium.focusWithTab(elFilterTahakkukNo);
		selenium.setValue(elFilterTahakkukNo, tahakkukNo);		
		selenium.enter();
		waitProcessorGone();
		
		selenium.focusWithTab(elFilterBasvuruNo);
		selenium.setValue(elFilterBasvuruNo, dosyaNo);
		selenium.enter();
		waitProcessorGone();
		
		selenium.focusWithTab(elDekontNo);
		
	}


	public void downloadFile() {
		selenium.click(lnkDownload);
		selenium.sleep(10L);
		selenium.switchToNewTab();
		selenium.click(selenium.getWebDriver().findElement(By.cssSelector("#download")));
		selenium.sleep(10L);
		
	}

}
