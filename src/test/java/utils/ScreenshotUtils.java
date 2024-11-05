package utils;

import com.google.common.io.Files;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class ScreenshotUtils {
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destination = "./screenshots/" + screenshotName + ".png";
        try {
            Files.copy(source.toPath().toFile(), (OutputStream) Paths.get(destination));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }
}
