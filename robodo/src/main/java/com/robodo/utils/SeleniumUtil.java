package com.robodo.utils;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import com.google.common.base.Splitter;
import com.robodo.model.ProcessInstance;

public class SeleniumUtil {
	RunnerUtil runnerUtil;
	WebDriver webDriver;
	String loadedJavascriptcode=null;
	
	public SeleniumUtil(RunnerUtil runnerUtil) {
		this.runnerUtil=runnerUtil;
	}
	
	public void startWebDriver() {
		String path=this.runnerUtil.getEnvironmentParameter("selenium.webdriver.path");
		String imlicitWaitStr=this.runnerUtil.getEnvironmentParameter("selenium.webdriver.implicitwait");
		System.setProperty("webdriver.chrome.driver",path);
		
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		
		var driver=new ChromeDriver(ops);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.valueOf(imlicitWaitStr)));
		this.webDriver = driver;
	}
	
	public void stopDriver() {
		if (this.webDriver==null) {
			return;
		}
		
		try {
			this.webDriver.close();;
		} catch(Exception e) {
		}
	}

	public void navigate(String url) {
		runnerUtil.logger("naviget to : %s".formatted(url));
		webDriver.get(url);
	}

	public void setValue(WebElement el, String value) {
		runnerUtil.logger("set value of %s to : %s".formatted(el2Str(el), value));

		el.clear();
		el.sendKeys(value);	
	}
	
	public void scrollToElement(WebElement el) {
		runnerUtil.logger("scroll to element : %s".formatted(el2Str(el)));

		((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", el);
		sleep(1L); 
	}
	
	public void scrollDownABit() {
		runnerUtil.logger("scrolling a bit %s");
		((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0,250)", "");
		sleep(1L); 
	}
	
	public void copyPasteByRobot(String value) {
		try {
			String property = System.getProperty("java.awt.headless");
			if (!property.equals("false")) {
				System.setProperty("java.awt.headless", "false");
			}
			
			
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(new StringSelection(value), null);
	        
	        logger("paste value from clipboard : %s".formatted(value));
			
			Robot robot=new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			Thread.sleep(200); 
			robot.keyPress(KeyEvent.VK_C); 
			Thread.sleep(200); 
			robot.keyRelease(KeyEvent.VK_C); 
			Thread.sleep(200); 
			robot.keyRelease(KeyEvent.VK_CONTROL); 
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("exception sendKeysByRobot :%S ".formatted(e.getMessage()));
		}
	}
	
	private String el2Str(WebElement el) {
		if (el==null) return "null";
		return el.getTagName()+"."+el.getAttribute("class");
	}

	private Keys encodeKey(String key) {
		switch (key) {
		case "ENTER": return Keys.ENTER;
		case "TAB": return Keys.TAB;
		case "ARROW_DOWN": return Keys.ARROW_DOWN;
		case "ARROW_UP": return Keys.ARROW_UP;
		case "PAGE_DOWN": return Keys.PAGE_DOWN;
		case "PAGE_UP": return Keys.PAGE_UP;
		default:
			throw new IllegalArgumentException("Unexpected value: " + "enter");
		}
	}
	
	public void pressKey(String key) {
		runnerUtil.logger("pressKey %s".formatted(key));

		Keys encodeKey = encodeKey(key);
		Actions builder = new Actions(webDriver);
		builder.sendKeys(encodeKey).build().perform();
	}

	public void sleep(Long seconds) {
		runnerUtil.logger("sleep %s seconds".formatted(String.valueOf(seconds)));

		Actions builder = new Actions(webDriver);
		builder.pause(seconds * 1000).build().perform();
	}

	public void click(WebElement el) {
		runnerUtil.logger("click : %s".formatted(el2Str(el)));

		el.click();
		
	}


	public String screenShot(ProcessInstance processInstance) {
		String targetDir=runnerUtil.getTargetPath(processInstance);
		TakesScreenshot scrShot =((TakesScreenshot) webDriver);
		File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
		new File(targetDir).mkdirs();
		String fileName="SS_"+System.currentTimeMillis()+".PNG";
		String fileWithPath=targetDir+File.separator+fileName;
		File DestFile=new File(fileWithPath);
		try {
			FileUtils.copyFile(SrcFile, DestFile);
			runnerUtil.logger("SCREENSHOT::%s".formatted(fileWithPath));
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void executeJavascript(WebElement el, String javaScript, String arguments)  {
		JavascriptExecutor executor = (JavascriptExecutor) webDriver;
		
		Object[] valuesExtracted =Splitter.on(",").trimResults().splitToList(arguments).stream().filter(p->p!=null && !p.isBlank()).map(p->{
			return this.runnerUtil.hmExtractedValues.get(p);
		}).toArray();
		runnerUtil.logger("execjs : %s for element %s".formatted(javaScript,el2Str(el)));

		executor.executeScript(javaScript,el,  valuesExtracted);
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void logger(String logstr) {
		runnerUtil.logger(logstr);
	}

	public void enter() {
		pressKey("ENTER");
		
	}

	public void tab() {
		pressKey("TAB");
		
	}
	
	public void switchIframe(Predicate<WebElement> filter) {
		runnerUtil.logger("switching to iframe by filter: %s".formatted(filter.toString()));
		List<WebElement> iframes = webDriver.findElements(By.tagName("iframe"));
		Optional<WebElement> opt = iframes.stream().filter(filter).findAny();
		if (opt.isEmpty()) {
			throw new RuntimeException("no iframe found to switch");
		}
		webDriver.switchTo().frame(opt.get());
	}
	
	public void switchToMainFrame() {
		runnerUtil.logger("switch to main frame");
		webDriver.switchTo().defaultContent();
	}

}