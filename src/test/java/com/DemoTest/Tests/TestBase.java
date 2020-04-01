package com.DemoTest.Tests;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

import org.json.simple.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.UnexpectedException;

public  class TestBase  {


    private static final String  targetEnvironment = null;
    private static final int sauce = 0;
	public String buildTag = System.getenv("BUILD_TAG");
    public static String username = System.getenv("SAUCE_USERNAME");
    public static String accesskey = System.getenv("SAUCE_ACCESS_KEY");

    public static final String sauceURL = "https://" +username+ ":" +accesskey+ "@ondemand.us-west-1.saucelabs.com/wd/hub";

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return Two dimensional array of objects with browser, version, and platform information
     */
    @DataProvider(name = "Browsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{

        	 // windows 10 
        	 
        	 new Object[]{"chrome", "latest", "Windows 10"},

     			 // windows 8.1
     			new Object[]{"firefox", "latest", "Windows 8.1"},
     			new Object[]{"chrome", "latest", "Windows 8.1"},
     			new Object[]{"firefox", "latest -1", "Windows 8.1"},
     			new Object[]{"chrome", "latest -1", "Windows 8.1"},
     
     			
     			new Object[]{"safari", "latest", "macOS 10.14"}, //12.0 safari version
     			
     			
     			
     			// windows 8
     			new Object[]{"chrome", "latest", "Windows 8"},
     			
     			// windows 7
     		
     			new Object[]{"firefox", "latest", "Windows 7"},
     			new Object[]{"chrome", "latest", "Windows 7"},
                
        };
    }

    @DataProvider(name = "Emulators", parallel = true)
       	    public static Object[][] sauceBrowserDataProviderEmulator(Method testMethod) {
       	        return new Object[][]{

       	                //
       	        	
       	         new Object[]{"Android GoogleAPI Emulator", "portrait", "Chrome", "8.0", "Android"},
             //    new Object[]{" Samsung Galaxy Tab A 10 GoogleAPI Emulator", "landscape", "Chrome", "8.1", "Android"},
       	             
       	        };
       	    }

    @DataProvider(name = "Simulators", parallel = true)
    public static Object[][] sauceBrowserDataProviderSimulators(Method testMethod) {
        return new Object[][]{

                new Object[]{"iPhone XS Simulator", "portrait", "Safari", "13.2", "iOS"},

        };
    }

    @DataProvider(name = "Devices", parallel = true)
    public static Object[][] sauceBrowserDataProviderRDC(Method testMethod) {
        return new Object[][]{
                //Verify that your account has access to the devices below
                new Object[]{"iOS", "iPhone 7", "10"},
                new Object[]{"iOS", "iPhone SE", ""}
        };
    }

    public WebDriver getWebDriver() {
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @param methodName Represents the name of the test case that will be used to identify the test on Sauce.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    protected void createDriverSimulator(String DeviceName, String orientation, String browser, String os, String platformName, String methodName)  throws MalformedURLException, UnexpectedException {

            DesiredCapabilities caps = new DesiredCapabilities();
            // set desired capabilities to launch appropriate browser on Sauce
            caps.setCapability("deviceName",DeviceName);
            caps.setCapability("deviceOrientation", orientation);
            caps.setCapability("browserName", browser);
            caps.setCapability("platformVersion",os);
            caps.setCapability("name", methodName);
            caps.setCapability("tage", Constants.tagSimulator);

            webDriver.set(new RemoteWebDriver(new URL(sauceURL), caps));

        // set current sessionId
            String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
            sessionId.set(id);
    }

    protected void createDriverEmulator(String DeviceName, String orientation, String browser, String os, String platformName, String methodName)  throws MalformedURLException, UnexpectedException {

        DesiredCapabilities caps = new DesiredCapabilities();
        // set desired capabilities to launch appropriate browser on Sauce
        caps.setCapability("deviceName",DeviceName);
        caps.setCapability("deviceOrientation", orientation);
        caps.setCapability("browserName", browser);
        caps.setCapability("platformVersion",os);
        caps.setCapability("name", methodName);
        caps.setCapability("tage", Constants.tagEmulator);

        // Launch remote browser and set it as the current thread
        //       webDriver.set(new RemoteWebDriver(new URL("https://" +Constants.sauceUsername+ ":" +Constants.saucePassword+ "@ondemand.saucelabs.com:443/wd/hub"), capabilities));
        webDriver.set(new RemoteWebDriver(new URL(sauceURL), caps));

        // set current sessionId
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
    }


    protected void createDriverNew(String browser, String version, String os, String methodName)  throws MalformedURLException, UnexpectedException {

        JSONObject obj = new JSONObject();
        obj.put("executable",Constants.preRunScriptFile);
        obj.put("background","false");

        DesiredCapabilities capabilities = new DesiredCapabilities();
                // set desired capabilities to launch appropriate browser on Sauce
                capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
                capabilities.setCapability(CapabilityType.VERSION, version);
                capabilities.setCapability(CapabilityType.PLATFORM, os);
                capabilities.setCapability("name", methodName);
                capabilities.setCapability("extendedDebugging", Constants.isDebugFlag);
                capabilities.setCapability("tags", Constants.tag);
                capabilities.setCapability("build", Constants.buildNumber);
                //      capabilities.setCapability("TunnelIdentifier", Constants.tunnelIdentifier);
                capabilities.setCapability("prerun", obj);

        // Launch remote browser and set it as the current thread
        //       webDriver.set(new RemoteWebDriver(new URL("https://" +Constants.sauceUsername+ ":" +Constants.saucePassword+ "@ondemand.saucelabs.com:443/wd/hub"), capabilities));
        webDriver.set(new RemoteWebDriver(new URL(sauceURL), capabilities));

        // set current sessionId
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
    }

    protected void createDriver(String browser, String version, String os, String methodName)  throws MalformedURLException, UnexpectedException {

        JSONObject obj = new JSONObject();
        obj.put("executable",Constants.preRunScriptFile);
        obj.put("background","false");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        // set desired capabilities to launch appropriate browser on Sauce
        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
        capabilities.setCapability(CapabilityType.VERSION, version);
        capabilities.setCapability(CapabilityType.PLATFORM, os);
        capabilities.setCapability("name", methodName);
        capabilities.setCapability("extendedDebugging", Constants.isDebugFlag);
        capabilities.setCapability("tags", Constants.tag);
        capabilities.setCapability("build", Constants.buildNumber);
  //      capabilities.setCapability("TunnelIdentifier", Constants.tunnelIdentifier);
        capabilities.setCapability("prerun", obj);

        // Launch remote browser and set it as the current thread
 //       webDriver.set(new RemoteWebDriver(new URL("https://" +Constants.sauceUsername+ ":" +Constants.saucePassword+ "@ondemand.saucelabs.com:443/wd/hub"), capabilities));
        webDriver.set(new RemoteWebDriver(new URL(sauceURL), capabilities));
            
        // set current sessionId
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
    }

    protected void createDriverRDC(String platformName, String platformVersion, String deviceName, String methodName)
            throws MalformedURLException, UnexpectedException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", deviceName);
        capabilities.setCapability("platformVersion", platformVersion);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("name",  methodName);
        // capabilities.setCapability("recordDeviceVitals", true);
        //  capabilities.setCapability("cacheId", "testDemo");
        //  capabilities.setCapability("noReset", true); // testobject_suite_name

        webDriver.set(new RemoteWebDriver(new URL(sauceURL), capabilities));

        // set current sessionId
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
    }


    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        ((JavascriptExecutor) webDriver.get()).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
        webDriver.get().quit();
    }

    protected void annotate(String text) {
        ((JavascriptExecutor) webDriver.get()).executeScript("sauce:context=" + text);
    }
}
