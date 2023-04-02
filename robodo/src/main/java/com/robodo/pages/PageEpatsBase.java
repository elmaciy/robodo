package com.robodo.pages;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsBase extends BasePage {



	public PageEpatsBase(SeleniumUtil selenium) {
		super(selenium);
	}

	public void setComboboxByTitleContains(WebElement elCombo, String islemAdi) {
		selenium.click(elCombo);
		selenium.sleep(1L);
		WebElement elIslemComboSearchInput=elCombo.findElement(By.xpath(".//input[@type='search']"));
		selenium.setValue(elIslemComboSearchInput, islemAdi);
		//selenium.copyPasteByRobot(islemAdi);
		selenium.tab();
	}
	
	
	

}
