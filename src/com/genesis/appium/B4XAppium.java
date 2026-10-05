package com.genesis.appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@ShortName(value = "B4XAppium")
@Author(value = "Walter Flores")
@Version(value = 1.29f)
@DependsOn(values = { "java-client-9.2.0.jar", "selenium-api-4.18.1.jar", "selenium-http-4.18.1.jar", 
		"selenium-java-4.18.1.jar", "selenium-remote-driver-4.18.1.jar", "selenium-support-4.7.2.jar", "guava-32.1.2-jre.jar" })

public class B4XAppium {
	
	private AndroidDriver driver;
	private IOSDriver iosDriver;
	private String mEventName = "";
	private AppiumDriver finaldriver;
	private WebElement el;
	private String platformUsed = "";
	
	public void Initialize(BA ba, String platform, String urlHost, int port, String basePath, DesiredCapabilitiesWrapper capabilities) throws MalformedURLException {
	    platformUsed = platform == null ? "" : platform.trim().toLowerCase();

	    String host = urlHost.endsWith("/") ? urlHost.substring(0, urlHost.length()-1) : urlHost;
	    String path = (basePath == null || basePath.isEmpty()) ? "/" : (basePath.startsWith("/") ? basePath : "/" + basePath);
	    if (!path.endsWith("/")) path += "/";

	    URL serverUrl = new URL(host + ":" + port + path);
	    BA.Log("URL: " + serverUrl);

	    if ("android".equals(platformUsed)) {
	        driver = new AndroidDriver(serverUrl, capabilities.getObject());
	        finaldriver = driver;
	    } else if ("ios".equals(platformUsed)) {
	        iosDriver = new IOSDriver(serverUrl, capabilities.getObject());
	        finaldriver = iosDriver;
	    } else {
	        throw new IllegalArgumentException("platform must be 'android' or 'ios'");
	    }
	}

	
	public boolean isInitialized() {
		return (finaldriver != null);
	}
	
	public void quit() {
	    try {
	        if (finaldriver != null) finaldriver.quit();
	    } finally {
	        driver = null;
	        iosDriver = null;
	        finaldriver = null;
	    }
	}
	
	public String getSessionId() {
		return finaldriver.getSessionId().toString();
	}
	
	public String getPageSource() {
		return finaldriver.getPageSource();
	}
	
	public void takeScreenShot(String filePath) {
		try {
			File srcFile = finaldriver.getScreenshotAs(OutputType.FILE);
			File destFile = new File(filePath);
			Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			BA.LogInfo("Screenshot saved to: " + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public WebElementWrapper findElementByName(BA ba, String elementName) {
		WebElement we = finaldriver.findElement(By.name(elementName));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByxpath(String text) {
		WebElement we = finaldriver.findElement(By.xpath("//*[@text='" + text + "']"));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	@SuppressWarnings({ "unused", "unused" })
	public WebElementWrapper findElementByXPathRaw(String xpath) {
	    WebElement we = finaldriver.findElement(By.xpath(xpath));
	    WebElementWrapper wew = new WebElementWrapper();
	    wew.setObject(we);
	    return wew;
	}
	
	public WebElementWrapper findElementByAndroidUIAutomator(String uiSelector) {
	    if (!"android".equals(platformUsed)) {
	        throw new IllegalStateException("Android UIAutomator is only supported on Android");
	    }
	    WebElement we = finaldriver.findElement(AppiumBy.androidUIAutomator(uiSelector));
	    WebElementWrapper wew = new WebElementWrapper();
	    wew.setObject(we);
	    return wew;
	}
	
	public WebElementWrapper findElementByid(String id) {
		WebElement we = finaldriver.findElement(By.id(id));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByTagName(String tagName) {
		WebElement we = finaldriver.findElement(By.tagName(tagName));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	public WebElementWrapper findElementByAccessibilityid(String id) {
		WebElement we = finaldriver.findElement(AppiumBy.accessibilityId(id));
		WebElementWrapper wew = new WebElementWrapper();
		wew.setObject(we);
		return wew;
	}
	
	private String xpathLiteral(String s) {
	    if (s == null) return "''";
	    if (!s.contains("'")) return "'" + s + "'";
	    // If contains single quotes, use concat('a', "'", 'b')
	    String[] parts = s.split("'");
	    StringBuilder sb = new StringBuilder("concat(");
	    for (int i = 0; i < parts.length; i++) {
	        if (i > 0) sb.append(", \"'\", ");
	        sb.append("'").append(parts[i]).append("'");
	    }
	    sb.append(")");
	    return sb.toString();
	}
	
	public WebElementWrapper findElementByText(String text) {
	    String lit = xpathLiteral(text);

	    String xpath;
	    if ("android".equals(platformUsed)) {
	        xpath = "//*[@text=" + lit + " or @content-desc=" + lit + "]";
	    } else {
	        xpath = "//*[@name=" + lit + " or @label=" + lit + "]";
	    }

	    WebElement we = finaldriver.findElement(By.xpath(xpath));
	    WebElementWrapper wew = new WebElementWrapper();
	    wew.setObject(we);
	    return wew;
	}
	
	public void Back() {
		finaldriver.navigate().back();
	}
	
	public WebElementWrapper tryFindElementById(String id) {
	    try {
	        return findElementByid(id);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public WebElementWrapper tryFindElementByText(String text) {
	    try {
	        return findElementByText(text);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public WebElementWrapper tryFindElementByAccessibilityId(String id) {
	    try {
	        return findElementByAccessibilityid(id);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public WebElementWrapper tryFindElementByAndroidUIAutomator(String uiSelector) {
	    try {
	        return findElementByAndroidUIAutomator(uiSelector);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public boolean waitForId(String id, int timeoutSec) {
	    try {
	        WebDriverWait wait = new WebDriverWait(finaldriver, Duration.ofSeconds(timeoutSec));
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	public boolean waitForText(String text, int timeoutSec) {
	    try {
	        String lit = xpathLiteral(text);
	        String xpath = "android".equals(platformUsed)
	            ? "//*[@text=" + lit + " or @content-desc=" + lit + "]"
	            : "//*[@name=" + lit + " or @label=" + lit + "]";
	        WebDriverWait wait = new WebDriverWait(finaldriver, Duration.ofSeconds(timeoutSec));
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	public void SwipeScreen(int startX, int startY, int endX, int endY, int durationMs) {
	    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
	    Sequence swipe = new Sequence(finger, 0);
	    swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
	    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
	    swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), endX, endY));
	    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
	    finaldriver.perform(Collections.singletonList(swipe));
	}

	public void SwipeUp(int durationMs) {
	    Dimension size = finaldriver.manage().window().getSize();
	    int startX = size.getWidth() / 2;
	    int startY = (int) (size.getHeight() * 0.80);
	    int endY = (int) (size.getHeight() * 0.20);
	    SwipeScreen(startX, startY, startX, endY, durationMs);
	}

	public void SwipeDown(int durationMs) {
	    Dimension size = finaldriver.manage().window().getSize();
	    int startX = size.getWidth() / 2;
	    int startY = (int) (size.getHeight() * 0.20);
	    int endY = (int) (size.getHeight() * 0.80);
	    SwipeScreen(startX, startY, startX, endY, durationMs);
	}

	public boolean ScrollToText(String text, int maxSwipes) {
	    for (int i = 0; i < maxSwipes; i++) {
	        if (tryFindElementByText(text) != null) return true;
	        SwipeUp(300);
	    }
	    return tryFindElementByText(text) != null;
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
		
		public String getAccessibleName() {
			return getObject().getAccessibleName();
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

}
