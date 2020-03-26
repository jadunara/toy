package kr.faz.app.example;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumChromeExample {
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";

	private WebDriver driver;
	private WebElement webElement;
	private String crawlingUrl = "";

	public static void main(String[] args) {

		SeleniumChromeExample sce = new SeleniumChromeExample();

		sce.craw();
	}

	public SeleniumChromeExample() {
		this.crawlingUrl = "https://finance.daum.net/domestic/market_cap?market=KOSPI";
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
		System.setProperty(WEB_DRIVER_ID, driverPath);
		driver = new ChromeDriver();
	}

	public void craw() {
		driver.get(crawlingUrl);
		String res = driver.getPageSource();
		System.out.println(res);
	}
}
