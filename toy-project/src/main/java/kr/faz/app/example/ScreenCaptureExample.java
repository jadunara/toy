package kr.faz.app.example;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This program demonstrates how to capture screenshot of a portion of screen.
 * url : https://www.codejava.net/java-se/graphics/how-to-capture-screenshot-programmatically-in-java
 * @author www.codejava.net
 *
 */
public class ScreenCaptureExample {

    public static void main(String[] args) {
    	processFull();
    	processPartial();
    }

	private static void processFull() {
        try {
            Robot robot = new Robot();
            String format = "jpg";
            String fileName = "d:/FullScreenshot." + format;

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, format, new File(fileName));

            System.out.println("A full screenshot saved!");
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
	}

	private static void processPartial() {
        try {
            Robot robot = new Robot();
            String format = "jpg";
            String fileName = "D:/PartialScreenshot." + format;

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle captureRect = new Rectangle(0, 0, screenSize.width / 2, screenSize.height / 2);
            BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
            ImageIO.write(screenFullImage, format, new File(fileName));

            System.out.println("A partial screenshot saved!");
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }

	}
}