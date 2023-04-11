package com.robodo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.robodo.utils.SeleniumUtil;

public class PageEpatsHome extends PageEpatsBase {
	
	@FindBy(xpath = "//a")
	WebElement elGirisEdevlet;

	public PageEpatsHome(SeleniumUtil selenium) {
		super(selenium);
	}

	public void clickEdevlet() {
		selenium.click(elGirisEdevlet);
		
	}

	public void open() {
		
		//bazen Edevlet girisi yerine zebralı giris ekrani geliyor
		//bunu asmak icin bu şekilde deneme yanılmalı bir döngü kurduk
		int counter=0;
		while(true) {
			if (counter++>=10) break;
			selenium.navigate("https://epats.turkpatent.gov.tr/");
			String currentUrl = selenium.getWebDriver().getCurrentUrl();
			if (currentUrl.contains("TP/EDEVLET/giris")) {
				break;
			}
			selenium.sleep(3L);
		}
		
		
		
	}

	public void navigateEdevletGiris() {
		int counter=0;
		while(true) {
			if (counter++>=10) break;
			selenium.navigate("https://epats.turkpatent.gov.tr/run/TP/EDEVLET/giris");
			String currentUrl = selenium.getWebDriver().getCurrentUrl();
			if (currentUrl.contains("TP/EDEVLET/giris")) {
				break;
			}
			selenium.sleep(3L);
		}


		
		
	}

}
