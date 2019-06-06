package com.applitools.genymotion;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

public class GenyParallel {

    private BatchInfo batch;
    private final static String APPIUM_SERVER_URL = "http://localhost:port/wd/hub";
    private final static String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private HashMap<String, AppiumDriver<MobileElement>> drivers = new HashMap<>();
    private HashMap<String, Eyes> allEyes = new HashMap<>();

    @DataProvider(name = "geny-device-provider", parallel = true)
    public Object[][] provide() throws Exception {
        return new Object[][]{
                {"4723", "localhost:5554", "8201", 2, 5},
                {"4724", "localhost:5556", "8202", 3, 6}};
    }

    @BeforeSuite
    public void setUp() {
        batch = new BatchInfo("AppliGeny");
    }

    @BeforeMethod
    public void beforeMethod(Object [] testArgs) {
        String methodName = ((Method) testArgs[0]).getName();
        ITestResult result = ((ITestResult) testArgs[1]);
        String appiumPort = testArgs[2].toString();
        String udid = (String) testArgs[3];
        String systemPort = (String) testArgs[4];

        log(String.format("Create AppiumDriver for - %s:%s, appiumPort - %s", udid, systemPort, appiumPort));
        AppiumDriver driver = createAppiumDriver(appiumPort, udid, systemPort);
        drivers.put(udid, driver);
        log(String.format("Created AppiumDriver for - %s:%s, appiumPort - %s", udid, systemPort, appiumPort));

        Eyes eyes = new Eyes();
        eyes.setApiKey(APPLITOOLS_API_KEY);
        eyes.setSaveNewTests(true);
        eyes.setMatchLevel(MatchLevel.EXACT);
        eyes.setBatch(batch);
        eyes.open(driver, "calculator", methodName + appiumPort);
        allEyes.put(udid, eyes);
    }

    @AfterMethod
    public void afterMethod(Object [] testArgs) {
        log(testArgs.toString());
        String methodName = ((Method) testArgs[0]).getName();
        ITestResult result = ((ITestResult) testArgs[1]);
        String appiumPort = testArgs[2].toString();
        String udid = (String) testArgs[3];
        String systemPort = (String) testArgs[4];

        Eyes eyes = allEyes.get(udid);
        AppiumDriver driver = drivers.get(udid);

        try {
            if (null != driver) {
                driver.quit();
            }

            TestResults testResult = eyes.close(false);
            log(String.format("Visual Validation Results for - %s:%s, appiumPort - %s", udid, systemPort, appiumPort));
            log(testResult);

            if (!testResult.isPassed()) {
                ITestContext tc = Reporter.getCurrentTestResult().getTestContext();
                tc.getPassedTests().addResult(result, result.getMethod());
                tc.getPassedTests().getAllMethods().remove(result.getMethod());
                result.setStatus(ITestResult.FAILURE);
                tc.getFailedTests().addResult(result, result.getMethod());

            }
        } catch (Exception e) {
            log("Exception - " + e.getMessage());
            e.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    @Test(dataProvider = "geny-device-provider", threadPoolSize = 2)
    public void runTest(Method method, ITestResult testResult, String appiumPort, String udid, String systemPort, int num1, int num2) {
        log(String.format("Runnng test on %s:%s, appiumPort - ", udid, systemPort, appiumPort));
        log(String.format("drivers.size()=%d, allEyes.size()=%d", drivers.size(), allEyes.size()));
        AppiumDriver driver = drivers.get(udid);
        Eyes eyes = allEyes.get(udid);
        try {
            eyes.checkWindow("Hello!");
            driver.findElement(By.id("digit_" + num1)).click();
            eyes.checkWindow("digit_" + num1);
            driver.findElement(By.id("op_add")).click();
            eyes.checkWindow("op_add");
            driver.findElement(By.id("digit_" + num2)).click();
            eyes.checkWindow("digit_" + num2);
            eyes.checkWindow("Answer");
            TestResults testResults = eyes.close();
            log(String.format("Visual Validation results for: %s:%s + %s", udid, systemPort, testResults));
        } catch (Exception e) {
            log(e.toString());
        } finally {
            if (null != driver) {
                driver.quit();
            }
            eyes.abortIfNotClosed();
        }
    }

    private void log(String message) {
        System.out.println(" ### " + new Date().toString() + " ### " + message);
    }

    private void log(TestResults testResult) {
        System.out.println(" ### " + new Date().toString() + " ### " + testResult);
    }

    private AppiumDriver createAppiumDriver(String appiumPort, String udid, String systemPort) {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Genymotion Cloud");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
            capabilities.setCapability(MobileCapabilityType.UDID, udid);
            capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, systemPort);
            capabilities.setCapability("appPackage", "com.android.calculator2");
            capabilities.setCapability("appActivity", ".Calculator");
            capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
            return new AppiumDriver<AndroidElement>(new URL(APPIUM_SERVER_URL.replace("port", appiumPort)), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in creating Appium Driver");
        }
    }
}