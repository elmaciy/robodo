package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsHizmetDokumu extends PageEpatsBase {

	public PageEpatsHizmetDokumu(SeleniumUtil selenium) {
		super(selenium);
	}

	@FindBy(xpath = "//*[text()='*Fatura Düzenlenecek Başvuru Sahibi']/..//div[@class='form-item-control' and contains(@ng-if,'selectbox')]")
	WebElement elComboBasvuruSahibi;

	@FindBy(xpath = "//div[text()='Devam Et >']")
	WebElement btDevam;

	public void basvuruSahibiSec(String code) {
		setComboboxByTitleContains(elComboBasvuruSahibi, code);

	}

	public void devamEt() {
		selenium.scrollDownABit();
		selenium.click(btDevam);
	}

}
