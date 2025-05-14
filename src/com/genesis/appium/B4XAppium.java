package com.genesis.appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

@ShortName(value = "B4XAppium")
@Author(value = "Walter Flores")
@Version(value = 1.18f)
@DependsOn(values = { "java-client-9.2.0.jar", "selenium-api-4.18.1.jar", "selenium-http-4.18.1.jar", 
		"selenium-java-4.18.1.jar", "selenium-remote-driver-4.18.1.jar" })

public class B4XAppium {
	
	private AndroidDriver driver;
	private String mEventName = "";
	
	private WebElement el;
	
	public void Initialize(BA ba, String EventName, String URLAddress, int port, DesiredCapabilitiesWrapper capabilities) throws MalformedURLException {
		mEventName = EventName.toLowerCase(BA.cul);
		//DesiredCapabilities caps = new DesiredCapabilities();
		//@SuppressWarnings("unchecked")
		//java.util.Map<String, Object> javaMap = (java.util.Map<String, Object>) capabilities.getObject();
		//for (java.util.Map.Entry<String, Object> entry : javaMap.entrySet()) {
		//	String key = entry.getKey();
		//	Object value = entry.getValue();
		//	caps.setCapability(key, value);
		//}
		driver = new AndroidDriver(new URL(URLAddress + ":" + port + "/"), capabilities.getObject());
	}
	
	public boolean isInitialized() {
		return (driver != null);
	}
	
	public void quit() {
		if (driver != null) {
			driver.quit();
		}
	}
	
	public void takeScreenShot(String filePath) {
		try {
			File srcFile = driver.getScreenshotAs(OutputType.FILE);
			File destFile = new File(filePath);
			Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			BA.LogInfo("Screenshot saved to: " + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public WebElementWrapper findElementByName(BA ba, String elementName) {
		WebElement we = driver.findElement(By.name(elementName));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByxpath(String text) {
		WebElement we = driver.findElement(By.xpath("//*[@text='" + text + "']"));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByTagName(String tagName) {
		WebElement we = driver.findElement(By.tagName(tagName));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByid(String id) {
		WebElement we = driver.findElement(By.id(id));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByAccessibilityid(String id) {
		WebElement we = driver.findElement(AppiumBy.accessibilityId(id));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	
	@ShortName(value = "WebElement")
	public static class WebElementWrapper extends AbsObjectWrapper<WebElement> {
		
		private String mEventName = "";
		WebElement el;
		
		public void Initialize(BA ba, String EventName) {
			mEventName = EventName.toLowerCase(BA.cul);
		}
		
		public void Click() {
			getObject().click();
		}
		
		public String getText() {
			return getObject().getText();
		}
		
		public void SendKeys(String input) {
			getObject().sendKeys(input);
		}
		
		public void Clear() {
			getObject().clear();
		}
		
	}
	
	@ShortName(value = "DesiredCapabilities")
	public static class DesiredCapabilitiesWrapper extends AbsObjectWrapper<DesiredCapabilities>{
		
		private DesiredCapabilities caps;
		
		public void Initialize(BA ba) {
			caps = new DesiredCapabilities();
			setObject(caps);
		}
		
		public void setCapability(BA ba, String key, Object value) {
			getObject().setCapability(key, value);
		}
		
	}
	
	@Hide
	public class ReportManager {
		
		private ExtentReports extent;
		private ExtentTest test;
		
		public void startReport(String reportPath) {
			ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportPath);
			extent = new ExtentReports();
			extent.attachReporter(htmlReporter);
		}
		
	    public void startTest(String testName) {
	        test = extent.createTest(testName);
	    }

	    public void logPass(String message) {
	        test.pass(message);
	    }

	    public void logFail(String message) {
	        test.fail(message);
	    }

	    public void addScreenshot(String imagePath) {
	        test.addScreenCaptureFromPath(imagePath);
	    }

	    public void endReport() {
	        extent.flush();
	    }
	}
	
	@ShortName("TestReport")
	public class ReportWrapper extends AbsObjectWrapper<ReportManager>{
		
		private ReportManager report;
		
		public void Initialize(String reportPath) {
			report = new ReportManager();
	        setObject(report);
	        getObject().startReport(reportPath);
	    }

	    public void StartTest(String name) {
	        getObject().startTest(name);
	    }

	    public void LogPass(String msg) {
	        getObject().logPass(msg);
	    }

	    public void LogFail(String msg) {
	        getObject().logFail(msg);
	    }

	    public void AddScreenshot(String path) {
	        getObject().addScreenshot(path);
	    }

	    public void EndReport() {
	        getObject().endReport();
	    }
	}

}
