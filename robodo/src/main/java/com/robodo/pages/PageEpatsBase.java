package com.robodo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsBase extends BasePage {



	public PageEpatsBase(SeleniumUtil selenium) {
		super(selenium);
	}

	public void setComboboxByTitle(WebElement elCombo, String islemAdi) {
		selenium.click(elCombo);
		WebElement elIslemComboSearchInput=elCombo.findElement(By.xpath("//input[@type='search']"));
		selenium.setValue(elIslemComboSearchInput, islemAdi);
		selenium.tab();
	}
}
