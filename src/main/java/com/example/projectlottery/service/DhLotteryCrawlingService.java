package com.example.projectlottery.service;

import com.example.projectlottery.domain.type.SidoType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DhLotteryCrawlingService {

    private static final String URL_WIN_INFO_645 = "https://www.dhlottery.co.kr/gameResult.do?method=byWin";
    private static final String URL_SELLER_INFO_645 = "https://www.dhlottery.co.kr/store.do?method=sellerInfo645";
    private static final String URL_WINNER_SELLER_INFO_645 = "https://www.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645";

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
     * 로또 6/45 회차별 당첨 번호
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @throws InterruptedException
     */
    public void getWin645(int start, int end) throws InterruptedException {
        setWebDriver();

        try {
            webDriver.get(URL_WIN_INFO_645); //url 이동
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


    /**
     * 로또 6/45 판매점 정보
     * @param sidoType 시도
     * @throws InterruptedException
     */
    public void getShop645(SidoType sidoType) throws InterruptedException {
        setWebDriver();

        if (sidoType == SidoType.ALL) {
            Arrays.stream(SidoType.values())
                    .filter(a -> a.ordinal() > 0)
                    .forEach(a -> {
                        try {
                            getShop645BySido(a);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } else {
            getShop645BySido(sidoType);
        }

        closeWebDriver();
    }

    /**
     * 로또 6/45 판매점 정보 - 시도별
     * @param sidoType 시도
     * @throws InterruptedException
     */
    private void getShop645BySido(SidoType sidoType) throws InterruptedException {
        webDriver.get(URL_SELLER_INFO_645); //url 이동
        Thread.sleep(500); //로딩될때까지 잠시 대기 (로딩 전 element 접근 시, 오류 발생)

        //서울의 경우 최초에 구 단위로 설정되어 있어서 타 시도로 이동했다가 다시 돌아오면 구 단위 필터가 풀리는 것을 발견해 이용
        if (sidoType == SidoType.SEOUL) {
            webDriver.findElement(By.cssSelector("#mainMenuArea > a:nth-child(2)")).click();
            Thread.sleep(500);
        }

        webDriver.findElement(By.cssSelector("#mainMenuArea > a:nth-child(" + sidoType.ordinal() + ")")).click();
        Thread.sleep(500);

        //마지막 페이지 번호 get
        int lastPageNo = getLastPageNoShop645ByArea();

        int curPage = 1;
        int idx = 0;

        while (curPage <= lastPageNo) {
            List<WebElement> pages = webDriver.findElements(By.cssSelector("#pagingView > a"));
            WebElement page = pages.get(idx++);

            if (page.getText().chars().allMatch(Character::isDigit)) { //[페이지 번호] 차례면, 판매점 크롤링 실행
                curPage++;
                page.click();
                Thread.sleep(500);

                List<WebElement> shopList = webDriver.findElements(By.cssSelector("#resultTable > tbody > tr"));
                System.out.println("----------------------------");
                for (WebElement shop : shopList) {
                    String shopName = shop.findElement(By.cssSelector("td:nth-child(1)")).getText();
                    String shopTel = shop.findElement(By.cssSelector("td:nth-child(2)")).getText();
                    String shopAddress = shop.findElement(By.cssSelector("td:nth-child(3)")).getText();
                    System.out.println(shopName + " " + shopTel + " " + shopAddress);    //⭐
                }
                System.out.println("----------------------------");

            } else if (page.getText().startsWith("다음")) { //[다음 페이지] 차례면, 다음 페이지 이동
                idx = 0;
                page.click();
                Thread.sleep(500);
            }
        }
    }

    /**
     * 마지막 페이지 번호 - 로또 6/45 판매점 정보 - 시도별
     * @return
     * @throws InterruptedException
     */
    private int getLastPageNoShop645ByArea() throws InterruptedException {
        List<String> pageNoList = webDriver.findElements(By.cssSelector("#pagingView > a"))
                .stream()
                .map(WebElement::getText)
                .toList();

        if (pageNoList.contains("끝 페이지")) {
            //끝 페이지로 이동
            webDriver.findElements(By.cssSelector("#pagingView > a")).get(pageNoList.indexOf("끝 페이지")).click();
            Thread.sleep(500);

            //마지막 페이지 번호 획득
            List<WebElement> pageList = webDriver.findElements(By.cssSelector("#pagingView > a"));
            int result = Integer.parseInt(pageList.get(pageList.size() - 1).getText());

            //다시 첫 페이지로 이동
            webDriver.findElements(By.cssSelector("#pagingView > a")).get(0).click();
            Thread.sleep(500);

            return result;

        } else {
            return Integer.parseInt(pageNoList.get(pageNoList.size() - 1));
        }
    }

    public void getWinningShop(int start, int end) throws InterruptedException {
        setWebDriver();

        webDriver.get(URL_WINNER_SELLER_INFO_645); //url 이동
        Thread.sleep(500); //로딩될때까지 잠시 대기 (로딩 전 element 접근 시, 오류 발생)

        for (int i = start; i <= end; i++) {
            //동행복권 사이트에서 최근 50회만 선택할 수 있으므로, 강제로 selected value 를 변경해 과거 정보를 가져온다.
            if (i < 1002) {
                WebElement element = webDriver.findElement(By.cssSelector("#drwNo > option:nth-child(1)"));
                JavascriptExecutor j = (JavascriptExecutor) webDriver;
                j.executeScript("arguments[0].value='" + i + "';", element);
            } else {
                //정상 제공되는 회차는 selected value 를 바꿔서 가져온다.
                Select select = new Select(webDriver.findElement(By.id("drwNo")));
                select.selectByValue(String.valueOf(i));
            }

            webDriver.findElement(By.id("searchBtn")).click();
            Thread.sleep(500);

            System.out.println("회차 = " + i);

            //1등 당첨 판매점
            get1stWinningLotteryShop(i);
            //2등 당첨 판매점
            get2ndWinningLotteryShop(i);
        }

        closeWebDriver();
    }

    /**
     * 로또 6/45 1등 당첨 판매점 정보
     * @param drawNo 추첨 회차
     * @throws InterruptedException
     */
    public void get1stWinningLotteryShop(int drawNo) throws InterruptedException {
        System.out.println("1등 당첨 판매점");

        List<WebElement> elements = webDriver.findElements(By.cssSelector("#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr"));

        for (int i = 1; i <= elements.size(); i++) {
            List<WebElement> elements1 = webDriver.findElements(By.cssSelector("#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr:nth-child(" + i + ") > td"));

            for (WebElement webElement : elements1) {
                System.out.print(webElement.getText() + " / ");
            }

            System.out.println();
        }

        System.out.println("----------------------------------------");
    }

    /**
     * 로또 6/45 2등 당첨 판매점 정보
     * @param drawNo 추첨 회차
     * @throws InterruptedException
     */
    public void get2ndWinningLotteryShop(int drawNo) throws InterruptedException {
        System.out.println("2등 당첨 판매점");

        int pageCount = webDriver.findElements(By.cssSelector("#page_box > a")).size();

        for (int i = 1; i <= pageCount; i++) {
            WebElement page = webDriver.findElement(By.cssSelector("#page_box > a:nth-child(" + i + ")"));
            page.click();
            Thread.sleep(500);

            int trCount = webDriver.findElements(By.cssSelector("#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr")).size();

            for (int j = 1; j <= trCount; j++) {
                List<WebElement> tdList = webDriver.findElements(By.cssSelector("#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr:nth-child(" + j + ") > td"));

                for (WebElement td : tdList) {
                    System.out.print(td.getText() + " / ");
                }

                System.out.println();
            }
        }

        System.out.println("----------------------------------------");
    }
}
