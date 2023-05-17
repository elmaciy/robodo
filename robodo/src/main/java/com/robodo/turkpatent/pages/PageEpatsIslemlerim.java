package com.robodo.turkpatent.pages;

import java.util.List;

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
	
	@FindBy(xpath = "//div[@class='form-item ']//div[@role='row']//div[@class='ui-grid-cell-contents']")
	public List<WebElement> cellContents;


	public void islemAra(String tahakkukNo, String dosyaNo) {
		selenium.focusWithTab(elFilterTahakkukNo);
		selenium.setValue(elFilterTahakkukNo, tahakkukNo);		
		selenium.enter();
		waitProcessorGone();
		
		selenium.focusWithTab(elFilterBasvuruNo);
		selenium.setValue(elFilterBasvuruNo, dosyaNo);
		selenium.enter();
		waitProcessorGone();
		
	}

}
