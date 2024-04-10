package com.example.projectlottery.api.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class SeleniumPurchaseService {
    @Value("${selenium.hub.purchase.url}")
    private String SELENIUM_HUB_URL;

    private static final int WAIT_RETRY_COUNT = 5; //wait 실패 시, retry 횟수 (5 * 100ms = 최대 500ms 대기)

    private WebDriver webDriver;

    private WebDriverWait webDriverWait;

    public void openWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");

        try {
            webDriver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), options);
            webDriverWait = new WebDriverWait(webDriver, Duration.ofMillis(100)); //최대 대기 100ms

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWebDriver() {
        try {
            if (webDriver != null) {
                webDriver.quit();
            }

        } catch (Exception e) {
            log.error("=== [Error] Exception occur to close web driver", e);

        } finally {
            webDriver = null;
            webDriverWait = null;
        }
    }

    public void openUrl(String url) {
        try {
            procJavaScript("location.href = \"" + url + "\""); //url 이동

            //main 창 외 모든 창 닫기
            String main = webDriver.getWindowHandle();
            for (String handle : webDriver.getWindowHandles()) {
                if(!handle.equals(main)) {
                    webDriver.switchTo().window(handle).close();
                }
            }

            //다시 main 창으로 변경
            webDriver.switchTo().window(main);
        } catch (Exception e) {
            log.error(e.getMessage());
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

    /**
     *  TODO: 다음 4 개의 method 는 하나로 합치는 것을 고려해보자.
     *  getElementById, getElementsById, getElementByCssSelector, getElementsByCssSelector
     */

    public WebElement getElementById(String id) {
        for (int i = 0; i < WAIT_RETRY_COUNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.id(id))
                );

            } catch (TimeoutException e) {
                log.warn("=== Element id `{}` not found, retrying... ({}/{})", id, (i + 1), WAIT_RETRY_COUNT);
            }
        }

        throw new NoSuchElementException("=== Element id `" + id + "` not found.");
    }

    public List<WebElement> getElementsById(String id) {
        for (int i = 0; i < WAIT_RETRY_COUNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id))
                );

            } catch (TimeoutException e) {
                log.warn("=== Element id `{}` not found, retrying... ({}/{})", id, (i + 1), WAIT_RETRY_COUNT);
            }
        }

        throw new NoSuchElementException("=== Element id `" + id + "` not found.");
    }

    public WebElement getElementByCssSelector(String css) {
        for (int i = 0; i < WAIT_RETRY_COUNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(css))
                );

            } catch (TimeoutException e) {
                log.warn("=== Css selector `{}` not found, retrying... ({}/{})", css, (i + 1), WAIT_RETRY_COUNT);
            }
        }

        throw new NoSuchElementException("=== Css selector `" + css + "` not found.");
    }

    public List<WebElement> getElementsByCssSelector(String css) {
        for (int i = 0; i < WAIT_RETRY_COUNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(css))
                );

            } catch (TimeoutException e) {
                log.warn("=== Css selector `{}` not found, retrying... ({}/{})", css, (i + 1), WAIT_RETRY_COUNT);
            }
        }

        throw new NoSuchElementException("=== Css selector `" + css + "` not found.");
    }


    public void procJavaScript(String script) {
        try {
            //TODO: purchaser 고도화 시, wait 처리 추가 (thread.sleep)
            ((JavascriptExecutor) webDriver).executeScript(script);
        } catch (Exception e) {
            //TODO: JavascriptException 던지도록 수정할 것
            throw new RuntimeException(e);
        }
    }

    public void procJavaScript(String script, Object... args) {
        try {
            //TODO: purchaser 고도화 시, wait 처리 추가 (thread.sleep)
            ((JavascriptExecutor) webDriver).executeScript(script, args);
        } catch (Exception e) {
            //TODO: JavascriptException 던지도록 수정할 것
            throw new RuntimeException(e);
        }
    }

    public String checkAlert() {
        String result = "";

        try {
            Alert alert = webDriver.switchTo().alert();
            result = alert.getText();
            alert.accept();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public void switchToIFrame(WebElement iFrameElement) {
        webDriver.switchTo().frame(iFrameElement);
    }

    public void switchToDefaultContent() {
        webDriver.switchTo().defaultContent();
    }
}
