package testFramework;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/***
 * Need to test if an event listener can be added into a web application by JavascriptExecutor
 * And then listened to in order to trigger a Selenium Webdriver method
 * (to keep things simple, let's not use Fitnesse)
 * @author gaguilar
 *
 */
public class WebAppEventListener {
	WebDriver driver;
	JavascriptExecutor jse;

	public WebAppEventListener() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initialize() throws Exception {
		System.out.println("initialize");
		//chromeDriverLocation == "the driver"
		//chromeBinaryLocation == "the car"
		/***
		 * From our resources folder, copy chromedriver.exe into a Driver folder
		 * Modify that chrome driver to attach to the chrome binary as designated in the Fitnesse table
		 */
		ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("chromedriver.exe");
		File chromedriver = new File("Driver"+"\\chromedriver.exe");
        if (!chromedriver.exists()) {
        	chromedriver.createNewFile();
            FileUtils.copyURLToFile(resource, chromedriver);
        }
		String chromeDriverLocation = chromedriver.getAbsolutePath();
        
		ChromeOptions options = new ChromeOptions();
		options.setBinary("C:\\GoogleChromePortable\\GoogleChromePortable.exe");
		options.addArguments("disable-infobars");
		options.addArguments("--allow-file-access-from-files");
		
		System.setProperty("webdriver.chrome.driver", chromeDriverLocation);              
		driver = new ChromeDriver(options);
		
//	    driver.get("http://localhost:8024/index.html?pet=Dog");
		driver.get("http://www.inhance.com");
//		driver.get("http://store.demoqa.com/");
	    waitForLoadToFinish();
	    
	    
//	    String xpathString = "//li[@id='menu-item-33']//a[@href='http://store.demoqa.com/products-page/product-category/']";
//	    WebElement el = driver.findElement(By.xpath(xpathString));
	    
	    //driver.findElements would have to be recreated to store xpaths of each element instead, why not use xslt?
	    List<WebElement> elList = driver.findElements(By.xpath("//*"));

	    
	    for(int i=0; i<elList.size(); i++) {
//	    	System.out.print(i+": " + elList.get(i).getTagName()+ "\t");
	    	System.out.print(i+": " + elList.get(i).getTagName()+ "\t");
	    	lookForEventListeners(elList.get(i));	    	
	    }
	}
	
	public void waitForLoadToFinish() {
		//wait for invisibility of a certain //div[@class='se-pre-con']
		//For now, sleep is fine
		System.out.println("waitForLoadToFinish()");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void lookForEventListeners(WebElement el) {
//		System.out.println("addEventListenerByJse()");
//		String javascriptCommand = "document.getElement(By.xpath(\"//div[@class='button bioVisualizer']\")).click()";
//		String javascriptCommand = 
//				"var getEventListeners = function(elem, type) {" + 
//				"  return getFromGlobalCache(elem, type);" + 
//				"};"+
//				"var list = getEventListeners(document.getElementById(\"navigation\")); console.log(list[\"focus\"][0][\"listener\"].toString());";
//		WebElement biovisualizer = driver.findElement(By.xpath("//div[@class=\"petView\"]"));
//		String xpathString = "//a[@href='http://demoqa.com/registration/']";
		
		
		JavascriptExecutor jseCopy = (JavascriptExecutor)driver;
//		jseCopy.executeScript("arguments[0].click();", biovisualizer);
//		jseCopy.executeScript(javascriptCommand);
//		jseCopy.executeScript("arguments[0].click();", biovisualizer);
//		jseCopy.executeScript("alert('Welcome To SoftwareTestingMaterial');");
//		String sText =  jseCopy.executeScript("return document").toString();
//		System.out.println(sText);
		
		
//		System.out.println(jseCopy.executeScript("return $(\".copyright\").text();"));
//		System.out.println(jseCopy.executeScript("return $(\"#petRoto\").text();"));
		isjQueryLoaded(driver);
		
//		System.out.println("-");
		String temp = jseCopy.executeScript("return jQuery._data(arguments[0], \"events\");", el)+"";
		if(!temp.equals("null")) {
//			System.out.println(el.toString());
			System.out.print(temp);
		}
		
		System.out.print("\n");
//		System.out.println("Done");
//		el.click();
//		driver.close();
//		biovisualizer.click();
	}
	
	public void isjQueryLoaded(WebDriver driver) {
//	    System.out.println("Waiting for ready state complete");
	    (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver d) {
	                JavascriptExecutor js = (JavascriptExecutor) d;
	                String readyState = js.executeScript("return document.readyState").toString();
//	                System.out.println("Ready State: " + readyState);
	                return (Boolean) js.executeScript("return !!window.jQuery && window.jQuery.active == 0");
	            }
	        });
	}
	
	public static void main(String[] args) {
		WebAppEventListener temp = new WebAppEventListener();
	}
}
