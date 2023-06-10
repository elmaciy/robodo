package com.robodo.turkpatent.pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
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
		selenium.sleep(5L);

		
	}


	public String islemPdfDosyasiIndir(Object downloadPath) {
		waitProcessorGone();
		String currentWindowHandle = selenium.getWindowHandle();
		selenium.click(lnkDownload);
		selenium.sleep(10L);
		
		selenium.switchToNextNewTab(currentWindowHandle);
		
		
		
		try {
			Robot r=new Robot();
			
			r.keyPress(KeyEvent.VK_CONTROL);
			selenium.sleep(1L);
			r.keyPress(KeyEvent.VK_S);
			
			r.keyRelease(KeyEvent.VK_S);
			r.keyRelease(KeyEvent.VK_CONTROL);
			
			selenium.sleep(3L);
			
			String filePath="%s\\%s.pdf".formatted(downloadPath,System.currentTimeMillis());
			StringSelection selection = new StringSelection(filePath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			
			
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);	
			r.keyRelease(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_CONTROL);
			
			r.keyPress(KeyEvent.VK_ENTER);
			r.keyRelease(KeyEvent.VK_ENTER);
			
			selenium.getWebDriver().close();
			
			selenium.switchToFirstTab();

			return filePath;
			
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
		


	}

}
