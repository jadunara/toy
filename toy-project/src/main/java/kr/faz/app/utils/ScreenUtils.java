package kr.faz.app.utils;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriver;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class ScreenUtils {
	public static void createWebPageScreenShot(String fileFullPath, WebDriver driver) throws IOException, Exception {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_END);
		robot.keyRelease(KeyEvent.VK_END);
		Thread.sleep(3000);
		robot.keyPress(KeyEvent.VK_HOME); // back to top page for next screen
		robot.keyRelease(KeyEvent.VK_HOME);
		Thread.sleep(1000);

		Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		ImageIO.write(fpScreenshot.getImage(), "PNG", new File(fileFullPath  + System.currentTimeMillis() + ".png"));

		Thread.sleep(1000);
		robot.keyPress(KeyEvent.VK_HOME); // back to top page for next screen
		robot.keyRelease(KeyEvent.VK_HOME);
		Thread.sleep(1000);

	}
}
