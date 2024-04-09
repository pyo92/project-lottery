package com.example.projectlottery.api.service;

import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.request.DhLoginRequest;
import com.example.projectlottery.dto.request.DhLottoPurchaseRequest;
import com.example.projectlottery.dto.response.DhDepositResponse;
import com.example.projectlottery.dto.response.DhLoginResponse;
import com.example.projectlottery.dto.response.DhLottoPurchaseResponse;
import com.example.projectlottery.service.RedisTemplateService;
import com.example.projectlottery.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseLotteryService {

    private static final String URL_DH_MAIN = "https://dhlottery.co.kr/common.do?method=main";
    private static final String URL_DH_LOGIN = "https://dhlottery.co.kr/user.do?method=login&returnUrl=";
    private static final String URL_PURCHASE_LIST = "https://dhlottery.co.kr/myPage.do?method=lottoBuyListView";
    private static final String URL_PURCHASE_L645 = "https://ol.dhlottery.co.kr/olotto/game/game645.do";
    private static final String URL_DH_DEPOSIT = "https://dhlottery.co.kr/payment.do?method=payment";

    private final SeleniumPurchaseService seleniumPurchaseService;

    private final RedisTemplateService redisTemplateService;

    /**
     * 동행복권 로그인
     *
     * @param request        로그인 정보(ID, 비밀번호) dto
     * @param webDriverClose 로그인 처리 후, web driver 종료 여부(타 로직 사용)
     * @return 로그인 결과 dto
     */
    public DhLoginResponse loginDhLottery(DhLoginRequest request, boolean webDriverClose) {
        seleniumPurchaseService.openWebDriver();
        seleniumPurchaseService.openUrl(URL_DH_LOGIN);

        String js;
        String css;

        //아이디 입력
        css = "#userId";
        WebElement idElement = seleniumPurchaseService.getElementByCssSelector(css);
        idElement.sendKeys(request.id());

        //비밀번호 입력
        css = "#article > div:nth-child(2) > div > form > div > div.inner > fieldset > div.form > input[type=password]:nth-child(2)";
        WebElement passwordElement = seleniumPurchaseService.getElementByCssSelector(css);
        passwordElement.sendKeys(request.password());

        //로그인 시도
        js = "check_if_Valid3();";
        seleniumPurchaseService.procJavaScript(js);

        //로그인 결과 체크
        String errorMessage = seleniumPurchaseService.checkAlert();
        if (!errorMessage.isEmpty()) { //로그인 실패
            seleniumPurchaseService.closeWebDriver(); //예외 시, web driver 를 종료하고 반환한다.
            return DhLoginResponse.of(false, errorMessage);
        }

        //로그인 성공 시, 메인 화면으로 이동
        seleniumPurchaseService.openUrl(URL_DH_MAIN);

        //이름 스크랩 (2024.01.15 수정)
        css = "body > div:nth-child(1) > header > div.header_con > div.top_menu > form > div > ul.information > li:nth-child(1) > span > strong";
        String name = seleniumPurchaseService.getElementByCssSelector(css).getText();
        name = name.substring(0, name.length() - 1);

        //예치금 스크랩
        css = "body > div:nth-child(1) > header > div.header_con > div.top_menu > form > div > ul.information > li.money > a:nth-child(2) > strong";
        Long deposit = Long.parseLong(seleniumPurchaseService.getElementByCssSelector(css).getText().replaceAll("[^0-9]", ""));

        //마이페이지 - 구매/당첨내역 페이지 이동
        seleniumPurchaseService.openUrl(URL_PURCHASE_LIST);
        css = "#lottoId";

        //검색 단위(로또 6/45) 설정
        Select lotterySelect = new Select(seleniumPurchaseService.getElementByCssSelector(css));
        lotterySelect.selectByValue("LO40");

        //검색 단위(1주일) 설정
        js = "changeTerm( 7, '1주일');";
        seleniumPurchaseService.procJavaScript(js);

        //검색
        css = "#submit_btn";
        WebElement submitElement = seleniumPurchaseService.getElementByCssSelector(css);
        submitElement.click();

        int purchasableCnt = 5; //온라인은 회차당 5매 제한이 있어서 구매 내역에서 조회된 매수만큼 빼줘야 한다.

        //구매내역 iframe 진입
        css = "#lottoBuyList";
        WebElement purchaseListFrame = seleniumPurchaseService.getElementByCssSelector(css);
        seleniumPurchaseService.switchToIFrame(purchaseListFrame); //iframe 으로 전환

        //구매내역 tr 을 iframe 내부에서 찾는다.
        css = "body > table > tbody > tr";
        List<WebElement> purchaseElements = seleniumPurchaseService.getElementsByCssSelector(css);
        for (WebElement e : purchaseElements) {
            if (e.getText().equals("조회 결과가 없습니다.")) break; //구매 내역이 전혀 없다면, loop exit

            String gameResult = e.findElement(By.cssSelector("td:nth-child(6)")).getText(); //당첨결과 column
            int purchaseCnt = Integer.parseInt(e.findElement(By.cssSelector("td:nth-child(5)")).getText()); //구입매수 column

            //미추첨인 구매 내역은 금번 회차 구매 내역이므로 잔여 구매가능 매수에서 차감한다.
            if (gameResult.equals("미추첨")) {
                purchasableCnt -= purchaseCnt;
            }
        }

        //iframe 을 빠져나온다.
        seleniumPurchaseService.switchToDefaultContent();

        //구매가능 매수가 없다면 구매창 진입을 막는다.
        if (purchasableCnt == 0) {
            seleniumPurchaseService.closeWebDriver(); //예외 시, web driver 를 종료하고 반환한다.
            return DhLoginResponse.of(false, "더 이상 구매할 수 없습니다.");
        }

        //여기까지 왔다면 정상 로그인 + 잔여 구매가능 매수도 1개 이상으로 정상 처리한다.

        //예치금 입금, 리프레시, 구매 기능에서 로그인이 선행되어야 하므로, 해당 메서드를 재사용한다.
        //그러기 위해서 webDriverClose 를 파라미터로 입력받아서 web driver 를 종료할 지 말 지 판단한다.
        if (webDriverClose) {
            seleniumPurchaseService.closeWebDriver();
        }

        //로그인 정보를 암호화해서 redis 에 저장한다.
        redisTemplateService.saveDhLoginInfo(request);

        //로그인 결과 반환
        return DhLoginResponse.of(true, errorMessage, request.id(), name, deposit, purchasableCnt);
    }

    /**
     * 동행복권 예치금 입금 신청
     *
     * @param dhLotteryId 동행복권 ID
     * @return 예치금 입금 신청 결과(계좌번호, 입금액) dto
     */
    public DhDepositResponse depositDhLottery(String dhLotteryId) {
        //로그인이 선행되어야 하므로 로그인 정보를 redis 에서 가져온다. (조회와 동시에 파기)
        DhLoginRequest dhLoginRequest = redisTemplateService.getDhLoginInfo(dhLotteryId);

        //redis 에 저장된 로그인 정보는 30분 뒤 자동으로 expire 된다.
        if (dhLoginRequest == null) {
            return null;
        }

        //로그인 처리 + 로그인 후에 입금신청 진행해야 하므로 web driver 를 닫지 않는다.
        loginDhLottery(dhLoginRequest, false);

        //예치금 신청 및 계좌번호 가져오는 루틴 시작

        //로그인이 선행되어서 driver 가 열린 상태이므로 열어줄 필요가 없다.
        seleniumPurchaseService.openUrl(URL_DH_DEPOSIT);

        //고정 가상계좌 입금을 선택한다. (2023.09.02 추가)
        String css = "#boxTabContent > h5.tab_box2 > a";
        seleniumPurchaseService.getElementByCssSelector(css).click();

        css = "#Amt";
        Select amountSelect = new Select(seleniumPurchaseService.getElementByCssSelector(css));
        amountSelect.selectByValue("5000"); //회차당 구매가능 게임이 5게임이므로 5000원으로 고정한다.

        //예치금 입금 신청
        String js = "nicepayStart();";
        seleniumPurchaseService.procJavaScript(js);

        //예치금 충전 정보 테이블을 가져온다.
        css = "#contents > table > tbody > tr";
        List<WebElement> depositInfo = seleniumPurchaseService.getElementsByCssSelector(css);
        String accountName = depositInfo.get(2).findElement(By.cssSelector("td")).getText();
        String accountNumber = depositInfo.get(3).findElement(By.cssSelector("td > span")).getText();
        Long depositAmount = Long.parseLong(depositInfo.get(1).findElement(By.cssSelector("td")).getText().replaceAll("[^0-9]", ""));

        seleniumPurchaseService.closeWebDriver();

        return DhDepositResponse.of(accountName, accountNumber, depositAmount);
    }

    /**
     * 동행복권 온라인 로또 구매
     *
     * @param dhLotteryId 동행복권 ID
     * @param request     로또 구매 요청 정보(번호) dto
     * @return 로또 구매 결과 dto
     */
    public DhLottoPurchaseResponse purchaseDhL645(String dhLotteryId, DhLottoPurchaseRequest request) {
        //로그인이 선행되어야 하므로 로그인 정보를 redis 에서 가져온다. (조회와 동시에 파기)
        DhLoginRequest dhLoginRequest = redisTemplateService.getDhLoginInfo(dhLotteryId);

        //redis 에 저장된 로그인 정보는 30분 뒤 자동으로 expire 된다.
        if (dhLoginRequest == null) {
            return null;
        }

        //로그인 처리 + 로그인 후에 구매를 진행해야 하므로 web driver 를 닫지 않는다.
        DhLoginResponse dhLoginResponse = loginDhLottery(dhLoginRequest, false);

        Integer[][] games = gameTokenizer(request);

        //구매 직전에 다시 한 번, 예치금과 구매가능 횟수를 체크한다.
        Long deposit = dhLoginResponse.deposit();
        Integer purchasableCount = dhLoginResponse.purchasableCount();

        if (games.length > purchasableCount) { //구매가능 매수 초과
            seleniumPurchaseService.closeWebDriver(); //예외 시, 닫아줘야 문제가 안생김
            return DhLottoPurchaseResponse.of(false, "더 이상 구매할 수 없습니다.");
        }

        if (games.length * 1000L > deposit) { //예치금 부족
            seleniumPurchaseService.closeWebDriver(); //예외 시, 닫아줘야 문제가 안생김
            return DhLottoPurchaseResponse.of(false, "예치금 잔액이 부족합니다.");
        }

        //온라인 로또 구매 루틴 시작

        //로그인이 선행되어서 driver 가 열린 상태이므로 열어줄 필요가 없다.
        seleniumPurchaseService.openUrl(URL_PURCHASE_L645);

        //자동화 툴에 의한 감지로, 비정상 접근 관련 메시지 창을 닫아준다.
        //창이 뜨지 않을 수도 있어서, 예외 처리
        String css = "#popupLayerAlert > div > div.btns > input";
        try {
            seleniumPurchaseService.getElementByCssSelector(css).click();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        String js;

        //사용자가 요청한 번호를 그대로 입력해서 구매를 진행한다.
        for (Integer[] game : games) {
            for (Integer i : game) {
                if (i == null) {
                    //번호가 없다면, 자동 선택이므로, 자동을 선택하고 loop 탈출
                    js = "$('#checkAutoSelect').click();";
                    seleniumPurchaseService.procJavaScript(js);
                    break;
                }

                //선택한 번호 클릭
                js = "$('#check645num" + i + "').click();";
                seleniumPurchaseService.procJavaScript(js);
            }

            //게임 추가 버튼 클릭
            js = "$('#btnSelectNum').click();";
            seleniumPurchaseService.procJavaScript(js);
        }

        //구매 처리
        js = "$('#btnBuy').click();";
        seleniumPurchaseService.procJavaScript(js);

        //"구매하시겠습니까?" 팝업 윈도우에 대한 "확인" 처리
        js = "closepopupLayerConfirm(true);";
        seleniumPurchaseService.procJavaScript(js);

        //구매내역 확인 레이어가 출력되어야 정상적으로 구매된 것이므로, 이를 체크
        css = "#report";
        WebElement purchaseResultElement = seleniumPurchaseService.getElementByCssSelector(css);
        String display = purchaseResultElement.getCssValue("display");

        DhLottoPurchaseResponse result = null;
        if (display.equals("none")) { //예치금부족 혹은 구매한도 초과로 인한 구매내역 확인 레이어가 뜨지 않은 경우, 오류 메시지 반환
            css = "#popupLayerAlert > div > div.noti > span";
            WebElement popupAlertElement = seleniumPurchaseService.getElementByCssSelector(css);

            result = DhLottoPurchaseResponse.of(false, popupAlertElement.getText());
        } else { //구매 성공
            //구매 내역 scrap (구매타입 + 번호)
            Map<String, LottoPurchaseType> lottoPurchaseTypeMap = new HashMap<>();
            Map<String, List<Integer>> lottoGameMap = new HashMap<>();

            css = "#reportRow > li";
            List<WebElement> gameElements = seleniumPurchaseService.getElementsByCssSelector(css);
            for (WebElement game : gameElements) {
                //map key
                String mapKey = game.findElement(By.cssSelector("strong > span:nth-child(1)")).getText();

                //구매 타입
                String purchaseType = game.findElement(By.cssSelector("strong > span:nth-child(2)")).getText();
                LottoPurchaseType lottoPurchaseType =
                        purchaseType.startsWith("자") ? LottoPurchaseType.AUTO :
                                (purchaseType.startsWith("수") ? LottoPurchaseType.MANUAL : LottoPurchaseType.MIX);

                lottoPurchaseTypeMap.put(mapKey, lottoPurchaseType);

                //구매 번호
                List<Integer> lottoGameNumbers = new ArrayList<>();
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(1)")).getText()));
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(2)")).getText()));
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(3)")).getText()));
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(4)")).getText()));
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(5)")).getText()));
                lottoGameNumbers.add(Integer.parseInt(game.findElement(By.cssSelector("div.nums > span:nth-child(6)")).getText()));

                lottoGameMap.put(mapKey, lottoGameNumbers);
            }

            result = DhLottoPurchaseResponse.of(true, null, lottoPurchaseTypeMap, lottoGameMap);
        }

        seleniumPurchaseService.closeWebDriver();

        return result;
    }

    /**
     * 로또 게임 dto parser - ajax 요청 시, 구분자로 구분된 문자열로 전달하므로 이를 해제
     *
     * @param request 구분자로 구분된 로또 게임 문자열 정보 dto
     * @return parsing 완료된 로또 게임 리스트
     */
    private Integer[][] gameTokenizer(DhLottoPurchaseRequest request) {
        List<String> games = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(request.game1())) games.add(request.game1());
        if (!StringUtils.isNullOrEmpty(request.game2())) games.add(request.game2());
        if (!StringUtils.isNullOrEmpty(request.game3())) games.add(request.game3());
        if (!StringUtils.isNullOrEmpty(request.game4())) games.add(request.game4());
        if (!StringUtils.isNullOrEmpty(request.game5())) games.add(request.game5());

        Integer[][] result = new Integer[games.size()][6];

        for (int i = 0; i < games.size(); i++) {
            result[i] = stringTokenizer(games.get(i));
        }

        return result;
    }

    /**
     * 로또 게임 string parser - ajax 요청 시, 구분자로 구분된 문자열로 전달하므로 이를 해제
     *
     * @param game 각 게임별 문자열
     * @return parsing 완료된 로또 게임
     */
    private Integer[] stringTokenizer(String game) {
        Integer[] result = new Integer[6];

        StringTokenizer st = new StringTokenizer(game, ",");

        int index = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!token.equals("?")) {
                result[index++] = Integer.parseInt(token);
            }
        }

        return result;
    }
}
