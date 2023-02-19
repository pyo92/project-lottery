package com.example.projectlottery.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChromeDriverService {

    private static WebDriver webDriver;

    public void openWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");

        ChromeOptions chromeOptions = new ChromeOptions();

        //브라우저 숨김 처리
        chromeOptions.addArguments("headless");
        //headless 에러 방지
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--disable-gpu");

        webDriver = new ChromeDriver(chromeOptions);
    }

    public void closeWebDriver() {
        webDriver.close();
        webDriver.quit();
    }

    public void openUrl(String url, long sleepTime) {
        try {
            webDriver.get(url); //url 이동

            //main 창 외 모든 창 닫기
            String main = webDriver.getWindowHandle();
            for (String handle : webDriver.getWindowHandles()) {
                if(!handle.equals(main)) {
                    webDriver.switchTo().window(handle).close();
                }
            }

            //다시 main 창으로 변경
            webDriver.switchTo().window(main);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchMainWindow(boolean toMain) {
        webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(toMain ? 0 : 1));
        if (toMain) {
            webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(1)).close();
            webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(0));
        } else {
            webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(1));
        }
    }

    public WebElement getElementById(String id) {
        return webDriver.findElement(By.id(id));
    }

    public List<WebElement> getElementsById(String id) {
        return webDriver.findElements(By.id(id));
    }

    public WebElement getElementByCssSelector(String css) {
        return webDriver.findElement(By.cssSelector(css));
    }

    public List<WebElement> getElementsByCssSelector(String css) {
        return webDriver.findElements(By.cssSelector(css));
    }


    public void procJavaScript(String script, long sleepTime) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript(script);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void procJavaScript(String script, WebElement element, long sleepTime) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript(script, element);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
