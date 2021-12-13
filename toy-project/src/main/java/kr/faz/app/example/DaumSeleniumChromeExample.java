package kr.faz.app.example;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.LineUnavailableException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v96.log.Log;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;

import kr.faz.app.utils.SoundUtils;

public class DaumSeleniumChromeExample {
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";

//	private WebDriver driver;
	private ChromeDriver driver;
//	private WebElement webElement;
	private String crawlingUrl = "";

	public static void main(String[] args) {

		DaumSeleniumChromeExample sce = new DaumSeleniumChromeExample();
		Selector s = null;
		try {
			sce.craw();
			s = Selector.open();
			s.select(3000);
			/// 
			sce.openDevTools();
//			while(true) {
//					sce.reload();
//					s.select(60*1000);
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( s != null )
				try {
					s.close();
				} catch (IOException e) {
				}
		}
	}


	//개발자 도구
	private void openDevTools() {
//		WebElement em = null;
		
//		String em;
//		while(true) {
//			try
//			{
//				em = driver.getPageSource() ; //
//				if ( em.indexOf("<legend class=\"screen_out\">통합 검색</legend>")!= -1 ) {
//					break;
//				}
//			} catch (Exception e) {
//				System.err.println(e.getMessage());
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//				continue;
//			}
//		}
//		WebElement x = driver.findElement(By.id("inner_login"));
//		WebElement x = driver.findElement(By.cssSelector("#inner_login > a.link_login.link_kakaoid"));
//		x.sendKeys(Keys.F12);
//		x.click();

		try {
			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_F12 + KeyEvent.VK_SHIFT );
			robot.keyPress( KeyEvent.VK_CONTROL);
			robot.keyPress( KeyEvent.VK_SHIFT );
			robot.keyPress( KeyEvent.VK_J );

			robot.keyRelease( KeyEvent.VK_CONTROL);
			robot.keyRelease( KeyEvent.VK_SHIFT );
			robot.keyRelease( KeyEvent.VK_J );
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	      DevTools devTools =  ((HasDevTools)driver).getDevTools();
	      devTools.createSession();
	      devTools.send(Log.enable());

	      devTools.addListener(Log.entryAdded(), logEntry -> {
	         System.out.println("-------------------------------------------");
	         System.out.println("Request ID = " + logEntry.getNetworkRequestId());
	         System.out.println("URL = " + logEntry.getUrl());
	         System.out.println("Source = " + logEntry.getSource());
	         System.out.println("Level = " + logEntry.getLevel());
	         System.out.println("Text = " + logEntry.getText());
	         System.out.println("Timestamp = " + logEntry.getTimestamp());
	         System.out.println("-------------------------------------------");
	      });
			LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
			logEntries.forEach(x ->{
				System.out.println(new Date(x.getTimestamp()) + " " + x.getLevel() + " " + x.getMessage());
			});
//	        for (LogEntry entry : logEntries) {
//	            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
//	            //do something useful with the data
//	        }
//	      driver.get("https://www.naver.com/");
			driver.get("https://whwl.tistory.com/42");
	}



//	private void login() throws InterruptedException {
//		driver.get("https://www.welkeepsmall.com/shop/member.html?type=login");
//		JavascriptExecutor js = (JavascriptExecutor) driver;
//		js.executeScript("sns_login_log('naver');");
//		WebElement loginButton = driver.findElement(By.id("log.login"));
//		WebElement idTxt = driver.findElement(By.id("id"));
//		WebElement pwTxt = driver.findElement(By.id("pw"));
//		idTxt.click();
//		Thread.sleep(1000);
////		driver.findElement(By.id("id")).sendKeys("");
//		pwTxt.click();
////		String pwd = "";
////
////		for ( char c : pwd.toCharArray()) {
////			Thread.sleep(1500);
////			driver.findElement(By.id("pw")).sendKeys(c+"");
////			idTxt.click();
////		}
//
//		Thread.sleep(30 * 1000);
////		js.executeScript("arguments[0].click();", loginButton);
//
//	}


	private void reload() throws Exception {
		try {
			String res = "";
			while( true ) {
				res = driver.getPageSource();

				if ( res.indexOf("<body>loading...") != -1 ) {
					Thread.sleep(1000);
				} else {
					break;
				}
			}

			//ScreenUtils.createWebPageScreenShot( "D:/welkeeps_FullPageScreenshot_", driver );

//			LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
//			for (LogEntry entry : logEntries) {
//			    System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
//			}

			Document doc = Jsoup.parse(res);
//			System.out.println(res);
			Elements ems = doc.select("#form1 > div > div.prd-btns");
			File file = new File("D:/welkeeps_2020_04_24.log");
			String logData = DateFormatUtils.format(new Date(),  "yyyy-MM-dd HH:mm:ss") + " " + ems.html();

			FileUtils.writeStringToFile(file, RegExUtils.replaceAll(RegExUtils.replaceAll(logData, "\r", ""), "\n", "")  +"\n", "UTF-8" , true);

			System.out.println(logData);

			if (ems.html().indexOf("SOLD OUT") != -1 ) {
				this.driver.navigate().refresh();
				return;
			} else if ( res.indexOf("<span jscontent=\"proxyTitle\" jstcache=\"25\">프록시 설정 변경") != -1) {
				Thread.sleep(30*1000);
				this.driver.navigate().refresh();
				return;
			}


			SoundUtils.tone(450,50, 0.01);
//			throw new Exception("주문가능... ");
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}



	public void craw() {
		driver.get(crawlingUrl);
	}

	public DaumSeleniumChromeExample() {
//		System.setProperty("webdriver.chrome.logfile", "D:\\chromedriver.log");//webdriver 로그 파일로..
//		System.setProperty("webdriver.chrome.verboseLogging", "true");//webdriver 로그 화면 콘솔
		this.crawlingUrl = "https://whwl.tistory.com/42";
		Map<String, String> env = System.getenv();
		Iterator<Entry<String, String>> itr = env.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		ClassLoader classLoader = this.getClass().getClassLoader();
//		URL res = classLoader.getResource("selenium/driver/chromedriver.80.0.3987.106.exe");
		URL res = classLoader.getResource("selenium/driver/chromedriver.96.0.4664.45.exe");
		String driverPath = res.getPath();

		if (SystemUtils.OS_NAME.toLowerCase().indexOf("windows") != -1 && driverPath.startsWith("/")) {
			driverPath = driverPath.substring(1);
		}

		// Setting your Chrome options (Desired capabilities)
//		ChromeOptions options = new ChromeOptions();
		//options.setPageLoadStrategy(PageLoadStrategy.NONE);

//		options.addArguments("--start-maximized");
//		options.addArguments("--start-fullscreen");

		System.setProperty(WEB_DRIVER_ID, driverPath);
//		driver = new ChromeDriver(options);
//		DesiredCapabilities capabilities = new DesiredCapabilities();
//		capabilities.setCapability("acceptInsecureCerts", true); // no dedicated method
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--auto-open-devtools-for-tabs" );//개발자 도구 열기
//		options.addArguments( "--webview-verbose-logging");//개발자 도구 열기
//		options.addArguments( "--trace-to-console");//개발자 도구 열기
//		options.addArguments( "--enable-logging");//개발자 도구 열기
//		options.addArguments( "--webview-log-js-console-messages");//개발자 도구 열기

		driver = new ChromeDriver(options);
		Dimension targetSize = new Dimension(1470, 1080);//window size(horizon, vertical)
		driver.manage().window().setSize(targetSize);
		
//		ChromeDriver driver = new ChromeDriver();
//		DevTools devTools = driver.getDevTools();		

	}
}
