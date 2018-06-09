package testFramework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
 * Webdriver.FindElements() does not keep track of the nested structure of elements
 * So jsoup will be used to parse html page and construct a navigatable tree to generate xpaths for all elements
 * The xpaths can then be used to get the elements EventHandlers
 * Those elements with click events (and other, yet to be determined interaction events) will then be listed and tested later for clicks
 * Note: Primary research (XSoup, JsoupXPath) allows you to input an xpath to get an element, not exaclty what we need
 * @author gaguilar
 *
 */
public class WebAppEventListener {
	WebDriver driver;
	JavascriptExecutor jse;
	Calendar calendar;	
	SimpleDateFormat sdf;
	String id;
	
	String javascriptString;
	
	Timestamp timestamp;
	String value;
	long startProcess;
	long endProcess;
	
	String loc;
	PrintWriter out;

	public WebAppEventListener() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initialize() throws Exception {
		startTimer("initialize");
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
		
		calendar = Calendar.getInstance();		
		sdf = new SimpleDateFormat("yyyyMMMdd");
		id = "qa_"+sdf.format(calendar.getTime());
		javascriptString= 
				"var type = document.createAttribute(arguments[1]);" + 
				"type.nodeValue = arguments[2];" + 
				"arguments[0].setAttributeNode(type);";
		
//		String baseUrl = "http://localhost:8024/index.html?pet=Dog";
//		String baseUrl = "http://www.inhance.com";
		String baseUrl = "http://store.demoqa.com/";
	    driver.get(baseUrl);
	    endTimer("initialize");
	    getDuration("initialize");
	    createLogFile();
	    
	    
	    waitForLoadToFinish();
	    
//	    String xpathString = "//li[@id='menu-item-33']//a[@href='http://store.demoqa.com/products-page/product-category/']";
//	    WebElement el = driver.findElement(By.xpath(xpathString));
	    
	    //driver.findElements would have to be recreated to store xpaths of each element instead, why not use xslt?
	    List<WebElement> elList = driver.findElements(By.xpath("//*"));
	    generateCustomIdForWebElementList(elList);
	    //Tree or dictionary?
	    //A Tree would be faster, but a dictionary would be easier to create
//	    Dictionary<WebElement, String> elDictionary = new Dictionary<WebElement, String>();//dictionary is deprecated in Java
//	    HashMap<String, WebElement> elMap = new HashMap<String, WebElement>();
//	    String xpathKey;
	    
	    
//	    for(int i=0; i<elList.size(); i++) {
//	    	xpathKey = getXpathOfWebElement(elList, i);
//	    	elMap.put(xpathKey, elList.get(i));
//	    }
	    
//	    for(int i=0; i<elList.size(); i++) {
////	    	System.out.print(i+": " + elList.get(i).getTagName()+ "\t");
//	    	System.out.print(i+": " + elList.get(i).getTagName()+ "\n");
//	    	lookForEventListeners(elList.get(i));	    	
//	    }

	    

	}
	
	protected void generateCustomIdForWebElementList(List<WebElement> elementList) {	
		//using xmlreader would give us our reader.Read()
		//can we just increment elementList as we go through the Doc?
		//but now, we have a possible issue with the doc and the elementlist DESYNC due to dynamic elements
		//Imagine a div being deleted, and another div is appended at the bottom
		//I really want to append a custom attribute to each element, but imagine appending an element with its own xpath, but then something previous to it changes
		//what we can do instead is create that custom attribute, and then do a WebDriver.findElement(By.cssSelector("[attributeName='qa06082018']");
		//the point for the qa with date attributename is that we want a name that the devs wouldn't normally use, but still have the attributeName be predictable.
		//For dynamic elements that come out, we'll have to capture them and tag them as we trigger them with clicks and maybe timing
		//If we use timestamps as the value of the attribute, and store those timestamps elsewhere, it may work
		//But then, what if the element with the timestamp disappears and we're trying to click it?
		//This issue can be approached by making each trigger independent of each other (or maybe through scanning for new elements?) 
		//The former would really slow us down, the latter may work with WebDriver.FindElements(), then find the ones without a timestamp?
		
		/* Plan:
		 * Using WebDriver.FindElements(), tag each element with a new timestamp, and add to a HashMap
		 * To find the clickable elements, iterate through the HashMap, and call lookForEventListeners on each
		 * */
		
//		Timestamp timestamp;
//		String attributeName;
//		String attributeValue;
		
//		URL url = new URL(baseUrl);
//		URLConnection connection = url.openConnection();//wouldn't this interfere with our driver?
//		connection.setDoOutput(true);

		WebElement el;

//		startTimer("tagging "+elementList.size()+" elements");
//		for(int i=0; i<elementList.size(); i++) {
//			//ugh, I just realized, how do I write to the current open html?
//			//We can try writing to a URLConnection, but that would interfere with our WebDriver
//			//Solution: Use javascriptexecutor to call container.appendChild
//			el = elementList.get(i);
//			tagElement(el);
////			lookForEventListeners(el);
//		}
//		endTimer("taggingElements");
//		getDuration("taggingElements");
//		
//		startTimer("lookingForAllEventListeners in "+elementList.size()+" elements");
//		for(int i=0; i<elementList.size(); i++) {
//			//ugh, I just realized, how do I write to the current open html?
//			//We can try writing to a URLConnection, but that would interfere with our WebDriver
//			//Solution: Use javascriptexecutor to call container.appendChild
//			el = elementList.get(i);
////			tagElement(el);
//			lookForEventListeners(el);
//		}
//		endTimer("lookingForAllEventListeners");
//		getDuration("lookingForAllEventListeners");
		
		startTimer("tag element and look for event handlers in "+elementList.size()+" elements");
		for(int i=0; i<elementList.size(); i++) {
			el = elementList.get(i);
			populateLogFile(tagElementAlt(el));
		}
		endTimer("tag element and look for event handlers in "+elementList.size()+" elements");
		getDuration("tag element and look for event handlers in "+elementList.size()+" elements");

//		return xpathOfWebElement;
	}
	
	public void createLogFile() {
        try {
        	loc ="C:\\WebAppEventListenerLog.txt";
        	out = new PrintWriter(loc);
	        out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void populateLogFile(String str) {
		//open out for appending
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(loc, true)));
//	    	out.println(appendedIndex + ": " + navigationPathAlternate.get(navArray.size()-1));
	    	out.append("\r\n\r\n" + str);
	    	out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	
	public void startTimer(String methodName) {
		startProcess = new Timestamp(System.currentTimeMillis()).getTime();
		System.out.println("start "+methodName+ ": " + startProcess);
	}
	
	public void endTimer(String methodName) {
		endProcess = new Timestamp(System.currentTimeMillis()).getTime();
		System.out.println("done " +methodName+ ": " + endProcess);
	}
	
	public void getDuration(String methodName) {
		System.out.println("duration of "+ methodName +": " + (endProcess - startProcess) + " msec");
	}
	
	/***
	 * Write a new attribute into our web element and keep a hashmap of tags
	 * qaDate = timestamp
	 * Below code has been moved to globals
	 * @param el
	 */
//	Calendar calendar = Calendar.getInstance();		
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMMdd");
//	String id = "qa_"+sdf.format(calendar.getTime());
//	
//	JavascriptExecutor jseCopy = (JavascriptExecutor)driver;
//	String javascriptString= 
//			"var type = document.createAttribute(arguments[1]);" + 
//			"type.nodeValue = arguments[2];" + 
//			"arguments[0].setAttributeNode(type);";
//	
//	Timestamp timestamp;
//	String value;
	
	public void tagElement(WebElement el) {		
		timestamp = new Timestamp(System.currentTimeMillis());
		value = timestamp.getTime()+"";
		JavascriptExecutor jseCopy = (JavascriptExecutor)driver;
		jseCopy.executeScript(javascriptString,  el, id, value);
	}
	
	//creating attribute with arguments[4] ends up becoming undefined for some reason, but the event handlers do get logged
	String startDataStr = "Gerard";
	public String tagElementAlt(WebElement el) {
		
		timestamp = new Timestamp(System.currentTimeMillis());
		value = timestamp.getTime()+"";
		JavascriptExecutor jseCopy = (JavascriptExecutor)driver;
		//the below should do: attach new attribute and set value, get all attributes, have the last entry be the eventhandlers
		javascriptString= 
				"var type = document.createAttribute(arguments[1]);" + 
				"type.nodeValue = arguments[2];" + 
				"arguments[0].setAttributeNode(type);" +
//				"var type2 = document.createAttribute(arguments[4]);" + 
//				"type2.nodeValue = jQuery._data(arguments[0], \"events\");" + 
//				"arguments[0].setAttributeNode(type2);" +
//				"var items = jQuery._data(arguments[0], \"events\"); return items;";
//				"var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;";
				"var items = {};"+
				"for (index = 0; index <arguments[0].attributes.length; ++index){"+
				"	items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value;"+
//				"	items[arguments[0].attributes[arguments[0].attributes.length].startData] = jQuery._data(arguments[0], \"events\");"+
				"};"+
				"var startData = arguments[4];"+
				"	items[arguments[4]] = jQuery._data(arguments[0], \"events\");"+
				"return items;";

		String attributesAndEvents = jseCopy.executeScript(javascriptString,  el, id, value, startDataStr)+"";
		return attributesAndEvents;
	}

	//This needs to be more responsive
	public void waitForLoadToFinish() {
		//wait for invisibility of a certain //div[@class='se-pre-con']
		//For now, sleep is fine
	    startTimer("waitForLoadToFinish");
		System.out.println("waitForLoadToFinish()");
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    endTimer("waitForLoadToFinish");
	    getDuration("waitForLoadToFinish");
	}
	
//	/***
//	 * Takes RAW Html and uses Jsoup to turn it into a document (meant for cleaning)
//	 */
//	public Document convertRawHtmlToDocumentUsingJsoup(String rawHtml) {
//		Document document = Jsoup.parse(rawHtml);
//		return document;
//	}
	
	//don't need to generate a copy of the html file
	//but we do need to create a parallel that we refer to in order to grab the xpath for reference
	//again, the point is to access everything by xpath AUTOMATICALLY
	//unless we use a crawler and generate the xpath when we encounter a new element in realtime instead of the pre-processing
	//which seems to be the better option since we need to consistently refresh the driver page anyway?
	public void convertJsoupDocumentToXml() {
		
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
//		isjQueryLoaded(driver);
		
//		System.out.println("-");
		//TODO: This line is what gives us all the registered events of an element 
		//jseCopy.executeScript("return jQuery._data(arguments[0], \"events\");", el)
		String temp = jseCopy.executeScript("return jQuery._data(arguments[0], \"events\");", el)+"";
//		String temp2 = el.getAttribute("innerHTML");
//		String temp3 = el.getText();
//		String temp4 = el.toString();
//		String temp5 = el.getAttribute("id");
//		String temp6 = el.getAttribute("class");
		String temp7 = getAllAttributesOfElement(el);
//		if(!temp.equals("null")) {
//			System.out.println(el.toString());
//			System.out.println("innerHTML: "+temp2);
//			System.out.println("getText(): "+temp3);
//			System.out.println("toString(): "+temp4);
			
//			System.out.println("events: "+temp);
//			System.out.println("attributes: "+temp7);
//			System.out.print("\n");
//		}
//		System.out.println("Done");
//		el.click();
//		driver.close();
//		biovisualizer.click();
	}
	
	//modified from https://stackoverflow.com/questions/36416796/selenium-webdriver-get-all-the-data-attributes-of-an-element
	protected String getAllAttributesOfElement(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		Object aa=executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", element);
		return aa.toString();
	}
	
	public void isjQueryLoaded(WebDriver driver) {
//	    System.out.println("Waiting for ready state complete");
		startTimer("isjQueryLoaded");
	    (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver d) {
	                JavascriptExecutor js = (JavascriptExecutor) d;
	                String readyState = js.executeScript("return document.readyState").toString();
//	                System.out.println("Ready State: " + readyState);
	                return (Boolean) js.executeScript("return !!window.jQuery && window.jQuery.active == 0");
	            }
	        });
		endTimer("isjQueryLoaded");
		getDuration("isjQueryLoaded");
	}
	
	public static void main(String[] args) {
		WebAppEventListener temp = new WebAppEventListener();
	}
}
