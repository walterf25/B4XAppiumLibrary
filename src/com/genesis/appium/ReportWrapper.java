package com.genesis.appium;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;

@ShortName("TestReport")
public  class ReportWrapper extends AbsObjectWrapper<com.genesis.appium.ReportWrapper.ReportManager>{
	
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
}