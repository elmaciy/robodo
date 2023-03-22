package com.robodo.pages;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.runner.SeleniumUtil;

public class PageEpatsMenu extends BasePage {
	
	@FindBy(css = "div.form-item-control div.btn")
	List<WebElement> elClickables;

	public PageEpatsMenu(SeleniumUtil selenium) {
		super(selenium);
	}
	

	private void clickableAction(String title) {
		selenium.logger("menü aç => %s".formatted(title));
		Optional<WebElement> opt = elClickables.stream().filter(p->p.getText().strip().equals(title)).findAny();
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
	
	public void cikis() {
		clickableAction("Sistemden Çıkış");		
	}
	
	

}
