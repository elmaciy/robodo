package com.robodo.turkpatent.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsTahakkuklarim extends PageEpatsBase {
		
	public PageEpatsMenu menu;
	
	
	@FindBy(xpath="//div[@title='Tahakkuk No']/..//input")
	WebElement elFilterTahakkukNo;

	@FindBy(xpath="//div[text()='Seçili Tahakkuku Öde']")
	WebElement elTahakkukOde;
	
	

	public PageEpatsTahakkuklarim(SeleniumUtil selenium) {
		super(selenium);
	}



	public void tahakkukNoAramaSecme(String tahakkukNo) {
		selenium.setValue(elFilterTahakkukNo, tahakkukNo);
		selenium.enter();
		List<WebElement> rows = selenium.getWebDriver().findElements(By.xpath("//div[@role='row'  and @ui-grid-row='row']//div[text()='%s ']".formatted(tahakkukNo)));
		if (rows.size()!=1) {
			throw new RuntimeException("tahakkukNoAramaSecme. %s nolu tahakkuk aramada sorun çıktı. Beklenen satır 1. Gelen satır %s".formatted(tahakkukNo,String.valueOf(rows.size())));
		}
		
		selenium.click(rows.get(0));
		selenium.sleep(1L);
	}
	
	public void tahakkukOde() {
		selenium.click(elTahakkukOde);
		
	}
}
