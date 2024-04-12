package com.example.projectlottery.api.service;

import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.DhLotteryLoginResult;
import com.example.projectlottery.dto.DhUserInfo;
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

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseLotteryService {

    /**
     * 1) 로그인, 당회차 구매 여부, 예치금 잔액 조회 - 모바일 페이지 사용
     * 2) 구매 - 모바일 페이지는 불가하므로, 데스크톱 페이지 사용
     */

    //로그인 직후 returnUrl 을 마이 페이지로 하여 최적화 시도
    private static final String URL_DH_LOGIN = "https://m.dhlottery.co.kr/user.do?method=loginm&returnUrl=https%3A%2F%2Fm.dhlottery.co.kr%2FuserSsl.do%3Fmethod%3DmyPage";
    private static final String URL_DH_DEPOSIT = "https://m.dhlottery.co.kr/payment.do?method=payment";
    private static final String URL_PURCHASE_L645 = "https://ol.dhlottery.co.kr/olotto/game/game645.do";

    private final SeleniumPurchaseService seleniumPurchaseService;

    private final RedisTemplateService redisTemplateService;

    /**
     * 동행복권 로그인
     *
     * @param request        로그인 정보(ID, 비밀번호) dto
     * @return 로그인 결과 dto
     */
    public DhLoginResponse loginDhLottery(DhLoginRequest request) {
        try {
            DhLotteryLoginResult loginResult = procLoginDhLottery(request);
            if (!loginResult.loginYn()) {
                return  DhLoginResponse.of(loginResult);
            }

            DhUserInfo userInfo = procScrapUserInfo(request.id());

            //구매가능 매수가 없다면 구매창 진입을 막는다.
            if (userInfo.purchasableCnt() == 0) {
                seleniumPurchaseService.closeWebDriver(); //web driver 를 종료하고 반환한다.
                return DhLoginResponse.of(false, "더 이상 구매할 수 없습니다.");
            }

            return DhLoginResponse.of(loginResult, userInfo);

        } catch (Exception e) {
            log.error("=== loginDhLottery() occurred error - {}", e.getMessage());

            throw new RuntimeException(e);

        } finally {
            seleniumPurchaseService.closeWebDriver();
        }
    }

    //다른 곳에서 재사용할 수 있도록 method 분리
    private DhLotteryLoginResult procLoginDhLottery(DhLoginRequest request) {
        try {
            seleniumPurchaseService.openWebDriver();
            seleniumPurchaseService.openUrl(URL_DH_LOGIN);

            String js;
            String css;

            //아이디 입력
            css = "#userId";
            WebElement idElement = seleniumPurchaseService.getElementByCssSelector(css);
            idElement.sendKeys(request.id());

            //비밀번호 입력
            css = "#password";
            WebElement passwordElement = seleniumPurchaseService.getElementByCssSelector(css);
            passwordElement.sendKeys(request.password());

            //로그인 시도
            js = "check_if_Valid3();";
            seleniumPurchaseService.procJavaScript(js);

            //로그인 결과 체크
            String errorMessage = seleniumPurchaseService.checkAlert();
            if (!errorMessage.isEmpty()) { //로그인 실패
                seleniumPurchaseService.closeWebDriver(); //web driver 를 종료하고 반환한다.
                return DhLotteryLoginResult.of(false, errorMessage);
            }

            //로그인 성공 시, redis 에 동행복권 로그인 정보를 저장한다.
            redisTemplateService.saveDhLoginInfo(request);

            return DhLotteryLoginResult.of(true); //로그인 성공 결과 반환

        } catch (Exception e) {
            log.error("=== doLogin() occurred error - {}", e.getMessage());
            seleniumPurchaseService.closeWebDriver();

            throw new RuntimeException(e);
        }
    }

    //TODO: util 클래스로 분리해준다. (재사용 가능성 O)
    private String getPreviousSunday() {
        LocalDate today = LocalDate.now();

        //오늘의 DayOfWeek 더해준다. (오늘을 포함한 과거의 가장 가까운 일요일) - 회차 시작일
        return today.minusDays(today.getDayOfWeek().getValue())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    //TODO: util 클래스로 분리해준다. (재사용 가능성 O)
    private String getNextSaturday() {
        LocalDate today = LocalDate.now();

        //토요일이 6 이므로, 오늘의 DayOfWeek 빼준다. (오늘을 포함한 미래의 가장 가까운 토요일) - 회차 종료일
        return today.plusDays(6 - today.getDayOfWeek().getValue())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    //다른 곳에서 재사용할 수 있도록 method 분리
    private DhUserInfo procScrapUserInfo(String id) {
        try {
            String css;

            css = "#header > div > ul > li > a:nth-child(1) > span > strong";
            String name = seleniumPurchaseService.getElementByCssSelector(css).getText();
            name = name.substring(0, name.length() - 1); //`님` 제거

            //예치금 스크랩
            css = "#container > div > div.myinfo_content.account > div.deposit > span > strong";
            Long deposit = Long.parseLong(seleniumPurchaseService.getElementByCssSelector(css).getText().replaceAll("[^0-9]", ""));

            int purchasableCnt = 5; //온라인은 회차당 5매 제한이 있어서 구매 내역에서 조회된 매수만큼 빼줘야 한다.

            //최근 7일간 구매내역 확인창 오픈
            MessageFormat iframeURL = new MessageFormat("https://dhlottery.co.kr/myPage.do?method=lottoBuyList&searchStartDate={0}&searchEndDate={1}&lottoId=LO40&nowPage=1");
            String startDt = getPreviousSunday();
            String endDt = getNextSaturday();
            seleniumPurchaseService.openUrl(iframeURL.format(new Object[] {startDt, endDt}));

            //구매내역 tr 을 찾는다.
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

            return DhUserInfo.of(id, name, deposit, purchasableCnt);

        } catch (Exception e) {
            log.error("=== scrapUserInfo() occurred error - {}", e.getMessage());
            seleniumPurchaseService.closeWebDriver();

            throw new RuntimeException(e);
        }
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
        procLoginDhLottery(dhLoginRequest);

        //예치금 신청 및 계좌번호 가져오는 루틴 시작

        //로그인이 선행되어서 driver 가 열린 상태이므로 열어줄 필요가 없다.
        seleniumPurchaseService.openUrl(URL_DH_DEPOSIT);

        //고정 가상계좌 입금을 선택한다. (2023.09.02 추가)
        String css = "#container > div > div.tab_ec > a:nth-child(2)";
        seleniumPurchaseService.getElementByCssSelector(css).click();

        css = "#Amt";
        Select amountSelect = new Select(seleniumPurchaseService.getElementByCssSelector(css));
        amountSelect.selectByValue("5000"); //회차당 구매가능 게임이 5게임이므로 5000원으로 고정한다.

        //예치금 입금 신청
        String js = "nicepayStart();";
        seleniumPurchaseService.procJavaScript(js);

        //예치금 충전 정보 테이블을 가져온다.
        css = "#container > div > div.complete_content > div > table > tbody > tr";
        List<WebElement> depositInfo = seleniumPurchaseService.getElementsByCssSelector(css);
        String accountName = depositInfo.get(2).findElement(By.cssSelector("td")).getText();
        String accountNumber = depositInfo.get(3).findElement(By.cssSelector("td > span")).getText();
        Long depositAmount = 5000L; //5000원으로 고정했다.

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
        //TODO: MSA 로 분리하면, SNS+SQS 사용할텐데, 구매 정보 던지고나면, 바로 db에 쌓아버리고,
        //  (rank 를 구분할 수 있게 쌓든지, 혹은 별도 컬럼을 하나 추가하든지 -> 구매중, 구매완료 이런식으로 플래그로?)
        //  selenium 에서 구매 완료되면 다시 구매완료로 플래그만 업데이트 처리하는 그런 방식도 괜찮을 것 같다.
        //  현재는, 구매완료 정보가 넘어올 때까지 사용자가 대기해야 하는? 그런 구조이다. 페이지를 이탈해도 상관없지만, UI 상 뭔가 기다리게 된다.
        //  그래서, 구매 시그널을 던지고, 구매내역 페이지로 이동시키면, 구매중으로 보일 것이고, 새로고침하면, 구매완료시 구매완료로 보이는? 그럼 안 기다릴 것 같다.

        try {
            //로그인이 선행되어야 하므로 로그인 정보를 redis 에서 가져온다. (조회와 동시에 파기)
            DhLoginRequest dhLoginRequest = redisTemplateService.getDhLoginInfo(dhLotteryId);

            //redis 에 저장된 로그인 정보는 30분 뒤 자동으로 expire 된다.
            if (dhLoginRequest == null) {
                return null;
            }

            //로그인 처리
            procLoginDhLottery(dhLoginRequest);

            //온라인 로또 구매 루틴 시작

            //로그인이 선행되어서 driver 가 열린 상태이므로 열어줄 필요가 없다.
            seleniumPurchaseService.openUrl(URL_PURCHASE_L645);

            //자동화 툴에 의한 감지로, 비정상 접근 관련 메시지 창을 닫아준다.
            //창이 뜨지 않을 수도 있어서, 예외 처리
            String css = "#popupLayerAlert > div > div.btns > input";
            try {
                seleniumPurchaseService.getElementByCssSelector(css).click();
            } catch (Exception e) {
                //
            }

            String js;

            //사용자가 요청한 번호를 그대로 입력해서 구매를 진행한다.
            Integer[][] games = gameTokenizer(request);

            for (Integer[] game : games) {
                for (Integer i : game) {
                    if (i == null) {
                        //TODO: type 추가 필요해보인다. 오류로 번호가 없는건지, 진짜 자동인지
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


            /**
             * 팝업 레이어별 정리 -> 각 레이어의 스타일 중 display: none 이 아닌 것을 찾으면 된다.
             * - #report : 정상 구매 완료
             * - #popupLayerAlert : 번호 미선택, 예치금 부족
             * - #recommend720Plus : 구매한도 초과
             *
             * 하지만, 성능을 위해 & 우리는 구매 성공 여부만 확인하면 된다.
             * report layer 만 체크함. report layer display none 이라면, 어떤 사유로든 구매실패
             */

            //구매내역 확인 레이어가 출력되어야 정상적으로 구매된 것이므로, 이를 체크
            css = "#report";
            WebElement purchaseResultElement = seleniumPurchaseService.getElementByCssSelector(css);

            if (purchaseResultElement.getCssValue("display").equals("none")) {
                //구매 실패
                return DhLottoPurchaseResponse.of(false, "더 이상 구매가 불가능합니다.");

            } else { //구매 성공
                //구매 내역 scrap (구매타입 + 번호)
                Map<String, LottoPurchaseType> lottoPurchaseTypeMap = new HashMap<>();
                Map<String, List<Integer>> lottoGameMap = new HashMap<>();

                css = "#reportRow > li";
                List<WebElement> gameElements = seleniumPurchaseService.getElementsByCssSelector(css);
                for (WebElement game : gameElements) {
                    //map key
                    String mapKey = game.findElement(By.cssSelector("strong > span:nth-child(1)")).getText();

                    //구매 타입 저장
                    String purchaseType = game.findElement(By.cssSelector("strong > span:nth-child(2)")).getText();
                    LottoPurchaseType lottoPurchaseType =
                            purchaseType.startsWith("자") ? LottoPurchaseType.AUTO :
                                    (purchaseType.startsWith("수") ? LottoPurchaseType.MANUAL : LottoPurchaseType.MIX);

                    lottoPurchaseTypeMap.put(mapKey, lottoPurchaseType);

                    //구매 번호 저장
                    lottoGameMap.put(
                            mapKey,
                            game.findElements(By.cssSelector("div.nums > span"))
                                    .stream()
                                    .map(e -> Integer.parseInt(e.getText()))
                                    .toList()
                    );
                }

                return DhLottoPurchaseResponse.of(true, null, lottoPurchaseTypeMap, lottoGameMap);
            }

        } catch (Exception e) {
            log.error("=== purchaseDhL645() occurred error - {}", e.getMessage());

            throw new RuntimeException(e);

        } finally {
            seleniumPurchaseService.closeWebDriver();
        }
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
