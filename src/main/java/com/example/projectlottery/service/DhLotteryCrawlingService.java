package com.example.projectlottery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DhLotteryCrawlingService {

    private static final String URL_WIN_INFO_645 = "https://www.dhlottery.co.kr/gameResult.do?method=byWin";

    private WebDriver webDriver;

    //TODO: 코드 중복을 줄이기 위해 Method extract 했으나, AOP 적용하면 좋을 것 같다.
    private void setWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");

        ChromeOptions chromeOptions = new ChromeOptions();

        //브라우저 보이지 않게
        chromeOptions.addArguments("headless");
        //headless 에러 방지
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--disable-gpu");

        webDriver = new ChromeDriver(chromeOptions);
    }

    //TODO: 코드 중복을 줄이기 위해 Method extract 했으나, AOP 적용하면 좋을 것 같다.
    private void closeWebDriver() {
        webDriver.close();
        webDriver.quit();
    }

    /**
     * 회차별 당첨번호
     */
    public void getWin(int start, int end) throws InterruptedException {
        setWebDriver();

        try {
            webDriver.get(URL_WIN_INFO_645);  //url 이동
            Thread.sleep(500); //로딩될때까지 잠시 대기 (로딩 전 element 접근 시, 오류 발생)

            for (int i = start; i <= end; i++) {
                //회차 selected value 를 변경
                Select select = new Select(webDriver.findElement(By.id("dwrNoList")));
                select.selectByValue(String.valueOf(i));

                webDriver.findElement(By.id("searchBtn")).click();
                Thread.sleep(500);

                System.out.println("회차 = " + i);

                //당첨번호
                List<WebElement> elements = webDriver.findElements(By.cssSelector("#article > div:nth-child(2) > div > div.win_result > div > div.num.win > p > span"));
                for (WebElement element : elements) {
                    System.out.println(element.getText() + " ");
                }

                //보너스번호
                WebElement bonus = webDriver.findElement(By.cssSelector("#article > div:nth-child(2) > div > div.win_result > div > div.num.bonus > p > span"));
                System.out.println("+ " + bonus.getText());

                System.out.println("--------------------------------------------------");
            }
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }

        closeWebDriver();
    }
}
