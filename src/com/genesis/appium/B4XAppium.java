package com.genesis.appium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import io.appium.java_client.android.AndroidDriver;

@ShortName(value = "B4XAppium")
@Author(value = "Walter Flores")
@Version(value = 1.10f)
@DependsOn(values = { "java-client-9.2.0.jar", "selenium-api-4.7.2.jar", "selenium-http-4.18.1.jar", 
		"selenium-java-4.18.1.jar", "selenium-remote-driver-4.7.2.jar", "selenium-support-4.7.2.jar" })

public class B4XAppium {
	
	private AndroidDriver driver;
	private String mEventName = "";
	
	private WebElement el;
	
	public void Initialize(BA ba, String EventName, DesiredCapabilitiesWrapper capabilities) throws MalformedURLException {
		mEventName = EventName.toLowerCase(BA.cul);
		//DesiredCapabilities caps = new DesiredCapabilities();
		//@SuppressWarnings("unchecked")
		//java.util.Map<String, Object> javaMap = (java.util.Map<String, Object>) capabilities.getObject();
		//for (java.util.Map.Entry<String, Object> entry : javaMap.entrySet()) {
		//	String key = entry.getKey();
		//	Object value = entry.getValue();
		//	caps.setCapability(key, value);
		//}
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), capabilities.getObject());
	}
	
	public boolean isInitialized() {
		return (driver != null);
	}
	
	public void quit() {
		if (driver != null) {
			driver.quit();
		}
	}
	
	public WebElementWrapper findElementByName(BA ba, String elementName) {
		WebElement we = driver.findElement(By.name(elementName));
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
