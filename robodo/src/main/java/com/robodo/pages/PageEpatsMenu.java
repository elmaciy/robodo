package com.robodo.pages;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.runner.SeleniumUtil;

public class PageEpatsMenu extends BasePage {

	public PageEpatsMenu(SeleniumUtil selenium) {
		super(selenium);
	}
	

	private void clickableAction(String title) {
		selenium.logger("menü aç => %s".formatted(title));
		Optional<WebElement> opt = selenium.getWebDriver().findElements(By.xpath("//div[text()='%s']".formatted(title))).stream().findAny();
		if (opt.isEmpty()) {
			throw new RuntimeException("menü bulunamadı : %s".formatted(title));
		}
		
		selenium.click(opt.get());
		
	}


	public void gotoBenimSayfam() {
		clickableAction("Benim Sayfam");
	}
	
	public void gotoBelgelerim() {
		clickableAction("Belgelerim");		
	}
	
	public void gotoTahakkuklarim() {
		clickableAction("Tahakkuklarım");	
		
	}
	
	
	public void cikis() {
		clickableAction("Sistemden Çıkış");		
	}


	public void gotoVekillikSayfam() {
		clickableAction("Vekillik Sayfam");		
		
	}


	
	

}
