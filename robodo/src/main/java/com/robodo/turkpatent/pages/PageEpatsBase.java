package com.robodo.turkpatent.pages;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.robodo.base.BasePage;
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
	
	private WebElement getGridContainerElement(String gridTitle) {
		String xpathforparent="//div[text()='%s']/../../../../..".formatted(gridTitle);
		WebElement elGridContainer = selenium.getWebDriver().findElement(By.xpath(xpathforparent));
		return elGridContainer;
	}
	
	public void setGridFilterByTitle(String gridTitle, String title, String value) {
		
		String xpath=".//span[text()='%s']/../..//input".formatted(title);
		WebElement elFilter = getGridContainerElement(gridTitle).findElement(By.xpath(xpath));
		selenium.setValue(elFilter, value);
		selenium.enter();
	}
	
	public void selectGridRowByUniqueContent(String gridTitle, String content) {
		String xpath=".//div[contains(@class,'ui-grid-cell-contents') and text()='%s']/../../..".formatted(content);
		WebElement element = getGridContainerElement(gridTitle).findElement(By.xpath(xpath));
		selenium.click(element);
	}
	
	public void selectGridFirstRow(String gridTitle) {
		String xpath=".//div[@style='overflow: scroll;']//div[@class='ui-grid-row']";
		List<WebElement> rows = getGridContainerElement(gridTitle).findElements(By.xpath(xpath));
		if (rows.size()>0) {
			selenium.click(rows.get(0));	
		} else {
			throw new RuntimeException("no rows to select in the grid, title = '%s'".formatted(gridTitle));
		}
		
		
	}
	
	public void waitProcessorGone() {
		WebDriverWait waiter=new WebDriverWait(selenium.getWebDriver(), selenium.getWebDriver().manage().timeouts().getImplicitWaitTimeout());
		try {
			List<WebElement> els = selenium.getWebDriver().findElements(By.cssSelector("div.sweet-overlay"));
			if (els.size()>0) {
				waiter.until(ExpectedConditions.invisibilityOf(els.get(0)));
			}
			
		} catch(Exception e) {
			
		}
		
		
	}
	
	public void uploadFileBySingleFileZone(WebElement el, String filePath) {
		selenium.sleep(3L);
		selenium.sendKeys(el,filePath);
		WebElement btYukle = selenium.getWebDriver().findElement(By.cssSelector("div.upload-container span.btn.btn-success"));
		selenium.click(btYukle);
		File file=new File(filePath);
		String fileName=file.getName();
		selenium.waitElementAttributeToBe(By.xpath("//i[@class='fa fa-file']/..//a"),"download",fileName);
	}

}
