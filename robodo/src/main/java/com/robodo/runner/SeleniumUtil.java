package com.robodo.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import com.google.common.base.Splitter;

public class SeleniumUtil {
	RunnerUtil runnerUtil;
	WebDriver webDriver;
	String loadedJavascriptcode=null;
	
	public SeleniumUtil(RunnerUtil runnerUtil) {
		this.runnerUtil=runnerUtil;
	}
	
	public void startWebDriver() {
		String path=this.runnerUtil.env.getProperty("selenium.webdriver.path");
		String imlicitWaitStr=this.runnerUtil.env.getProperty("selenium.webdriver.implicitwait");
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
		
		this.webDriver.quit();
		this.webDriver=null;
	}

	public void navigate(String url) {
		webDriver.get(url);
	}

	public WebElement locateElementByCss(String cssSelector) {
		return webDriver.findElement(By.cssSelector(cssSelector));
	}
	
	public WebElement locateElementByXpath(String xpathSelector) {
		return webDriver.findElement(By.xpath(xpathSelector));
	}

	public void setValue(WebElement locatedWebElement, String value) {
		locatedWebElement.sendKeys(value);	
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
		Keys encodeKey = encodeKey(key);
		Actions builder = new Actions(webDriver);
		builder.sendKeys(encodeKey).build().perform();
	}

	public void sleep(Long seconds) {
		Actions builder = new Actions(webDriver);
		builder.pause(seconds * 1000).build().perform();
	}

	public void click(WebElement el) {
		el.click();
		
	}

	public String extractElementAttribute(WebElement el, String attributeName) {
		if (attributeName.toLowerCase().equals("text")) {
			return el.getText();
		}
		
		return el.getAttribute(attributeName);
	}

	public void screenShot() {
		String targetDir=runnerUtil.getTargetPath();
		TakesScreenshot scrShot =((TakesScreenshot) webDriver);
		File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
		new File(targetDir).mkdirs();
		String fileWithPath=targetDir+File.separator+"SHOT_"+System.currentTimeMillis()+".PNG";
		File DestFile=new File(fileWithPath);
		try {
			FileUtils.copyFile(SrcFile, DestFile);
			runnerUtil.logger("SCREENSHOT::%s".formatted(fileWithPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadJavascript(String scriptFile) {
		String scriptHome=this.runnerUtil.env.getProperty("working.dir")+File.separator+"scripts";
		String scriptFilePath=scriptHome+File.separator+scriptFile;
		if (!scriptFilePath.toLowerCase().endsWith(".js")) {
			scriptFilePath=scriptFilePath+".js";
		}
		if (!new File(scriptFilePath).exists()) {
			throw new RuntimeException("loadJavascript : No file found : %s".formatted(scriptFilePath));
		}
		
		try {
			BufferedReader reader = new BufferedReader (new InputStreamReader(new FileInputStream(scriptFilePath), "UTF-8"));
			this.loadedJavascriptcode = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		    reader.close();
		} catch (Exception e) {
			throw new RuntimeException("loadJavascript : exception : %s".formatted(e.getMessage()));
		}
		
	}

	public void executeJavascript(String arguments)  {
		JavascriptExecutor executor = (JavascriptExecutor) webDriver;
		Object[] valuesExtracted =Splitter.on(",").trimResults().splitToList(arguments).stream().filter(p->p!=null && !p.isBlank()).map(p->{
			return this.runnerUtil.hmExtractedValues.get(p);
		}).toArray();
		
		executor.executeScript(this.loadedJavascriptcode,this.runnerUtil.locatedWebElement,  valuesExtracted);
	}

	public SearchContext getWebDriver() {
		return webDriver;
	}

	public void logger(String logstr) {
		runnerUtil.logger(logstr);
		
	}

}
