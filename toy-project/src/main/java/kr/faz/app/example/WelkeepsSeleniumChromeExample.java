package kr.faz.app.example;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import kr.faz.app.utils.SoundUtils;

public class WelkeepsSeleniumChromeExample {
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";

	private WebDriver driver;
	private WebElement webElement;
	private String crawlingUrl = "";

	public static void main(String[] args) {

		WelkeepsSeleniumChromeExample sce = new WelkeepsSeleniumChromeExample();
		Selector s = null;
		try {
			sce.craw();
			s = Selector.open();
			while(true) {
					sce.reload();
					s.select(60*1000);
			}

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



	@SuppressWarnings("deprecation")
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


			SoundUtils.tone(450,5000, 0.05);
			throw new Exception("주문가능... ");
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}



	public void craw() {
		driver.get(crawlingUrl);
	}

	public WelkeepsSeleniumChromeExample() {
		this.crawlingUrl = "http://www.welkeepsmall.com/shop/shopdetail.html?branduid=997662&xcode=023&mcode=002&scode=&type=X&sort=manual&cur_code=023002&GfDT=ZmV0";
		Map<String, String> env = System.getenv();
		Iterator<Entry<String, String>> itr = env.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		ClassLoader classLoader = this.getClass().getClassLoader();
		URL res = classLoader.getResource("selenium/driver/chromedriver.80.0.3987.106.exe");
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
		driver = new ChromeDriver();
		Dimension targetSize = new Dimension(1470, 1080);//window size(horizon, vertical)
		driver.manage().window().setSize(targetSize);

	}
}
