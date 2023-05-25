package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.PurchaseLotteryService;
import com.example.projectlottery.dto.request.DhLoginRequest;
import com.example.projectlottery.dto.request.DhLottoPurchaseRequest;
import com.example.projectlottery.dto.request.LottoGameRequest;
import com.example.projectlottery.dto.response.DhDepositResponse;
import com.example.projectlottery.dto.response.DhLoginResponse;
import com.example.projectlottery.dto.response.DhLottoPurchaseResponse;
import com.example.projectlottery.dto.response.LottoGameResponse;
import com.example.projectlottery.service.RedisTemplateService;
import com.example.projectlottery.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/purchase/dh")
@Controller
public class PurchaseController {

    private final PurchaseLotteryService purchaseLotteryService;
    private final RedisTemplateService redisTemplateService;

    /**
     * 동행복권 로그인 view
     * @param session http session (for response message)
     * @param map thymeleaf binding model map
     * @return 동행복권 로그인 view file name
     */
    @GetMapping("login")
    public String dhLoginForm(HttpSession session, ModelMap map) {
        //session attribute 에서 response dto 체크
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");
        if (dhLoginResponse != null && !dhLoginResponse.loginOk()) {
            map.addAttribute("dhLoginResponse", dhLoginResponse); //alert 표시를 위해 map binding
            session.removeAttribute("dhLoginResponse"); //binding 후에는 session attribute 에서 삭제
        }

        return "purchase/dhLogin";
    }

    /**
     * 동행복권 로그인 post 처리
     * @param request 로그인 request dto
     * @param session http session (for response message)
     * @return 로그인 결과에 따른 분기 view file name
     */
    @PostMapping("login")
    public String dhLogin(DhLoginRequest request, HttpSession session) {
        //동행복권 로그인 처리
        DhLoginResponse response = purchaseLotteryService.loginDhLottery(request, true);

        //response dto 를 session attribute binding
        session.setAttribute("dhLoginResponse", response);

        if (response.loginOk()) {
            if (response.purchasableCount() == 0) { //구매 가능이 0이면, 로그인 페이지로 이동
                return "redirect:/purchase/dh/login";
            } else {
                return "redirect:/purchase/dh/L645";
            }
        } else { //로그인 실패 시, 로그인 페이지로 이동
            return "redirect:/purchase/dh/login";
        }
    }

    /**
     * 로또 번호 선택 view
     * @param session http session (for response message)
     * @param map thymeleaf binding model map
     * @return 로또 번호 선택 view file name
     */
    @GetMapping("L645")
    public String purchaseLottoForm(HttpSession session, ModelMap map) {
        //session attribute 에서 로그인 response dto 체크
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        //로그인 정보가 존재하지 않거나, 실패한 경우는 다시 로그인 페이지로 되돌려 보낸다.
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return "redirect:/purchase/dh/login";
        }

        //로그인 response dto 의 예치금, 구매가능 정보 model map binding
        map.addAttribute("dhLoginResponse", dhLoginResponse);

        //session attribute 에서 구매 response dto 체크
        DhLottoPurchaseResponse dhPurchaseResponse = (DhLottoPurchaseResponse) session.getAttribute("dhPurchaseResponse");
        if (dhPurchaseResponse != null) {
            map.addAttribute("dhPurchaseResponse", dhPurchaseResponse);
        }

        return "purchase/purchaseLotto";
    }

    /**
     * 동행복권 로또 구매 post 처리
     * @param request 로또 구매 request dto
     * @param session http session (for response message)
     * @return 로또 구매 결과에 따른 분기 view file name
     */
    @PostMapping("L645")
    public String purchaseLotto(DhLottoPurchaseRequest request, HttpSession session) {
        //session attribute 에서 로그인 response dto 체크
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        //로그인 정보가 존재하지 않거나, 실패한 경우는 다시 로그인 페이지로 되돌려 보낸다.
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return "redirect:/purchase/dh/login";
        }

        //로또 구매 처리하기 전에 동행복권 로그인이 선행되어야 한다.
        //따라서, session attribute 에 있는 로그인 response dto 의 id 를 구매 처리 함수에 전달한다.
        DhLottoPurchaseResponse response = purchaseLotteryService.purchaseDhL645(dhLoginResponse.id(), request);

        //로그인 정보 expired 로 인한 구매 실패 처리
        if (response == null) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));

            return "redirect:/purchase/dh/login";
        }

        //구매 성공
        if (response.purchaseOk()) {
            session.removeAttribute("dhLoginResponse");

            //request, response dto 를 담아서 구매 결과 페이지로 보낸다.
            session.setAttribute("dhPurchaseRequest", request);
            session.setAttribute("dhPurchaseResponse", response);

            return "redirect:/purchase/dh/L645/result";
        }

        //예치금 부족 등 구매 실패 시 구매 페이지로 다시 redirection
        session.setAttribute("dhPurchaseResponse", response);
        return "redirect:/purchase/dh/L645";
    }

    /**
     * 동행복권 로또 구매 결과 view
     * @param session http session (for response message)
     * @param map thymeleaf binding model map
     * @return 로또 구매 결과 view file name
     */
    @GetMapping("L645/result")
    public String purchaseLottoResult(HttpSession session, ModelMap map) {
        //구매 내역(번호) 를 표시하기 위핸 request dto 와 구매 결과 response dto 를 가져온다.
        DhLottoPurchaseRequest request = (DhLottoPurchaseRequest) session.getAttribute("dhPurchaseRequest");
        DhLottoPurchaseResponse response = (DhLottoPurchaseResponse) session.getAttribute("dhPurchaseResponse");

        //request, response dto 가 존재하지 않으면, 루트 페이지로 보낸다.
        if (request == null || response == null) {
            return "redirect:/";
        }

        //구매 결과가 실패인 경우, 루트 페이지로 보낸다.
        if (!response.purchaseOk()) {
            return "redirect:/";
        }

        //회차를 표시하기 위해 회차 정보 model binding
        map.addAttribute("drawNo", request.drawNo());

        //구매 게임 내역에 대한 정보를 표시하기 위해 5개의 game model binding
        if (!StringUtils.isNullOrEmpty(request.game1())) {
            map.addAttribute("game1", Arrays.stream(request.game1().split(",")).map(Integer::parseInt).toList());
        }
        if (!StringUtils.isNullOrEmpty(request.game2())) {
            map.addAttribute("game2", Arrays.stream(request.game2().split(",")).map(Integer::parseInt).toList());
        }
        if (!StringUtils.isNullOrEmpty(request.game3())) {
            map.addAttribute("game3", Arrays.stream(request.game3().split(",")).map(Integer::parseInt).toList());
        }
        if (!StringUtils.isNullOrEmpty(request.game4())) {
            map.addAttribute("game4", Arrays.stream(request.game4().split(",")).map(Integer::parseInt).toList());
        }
        if (!StringUtils.isNullOrEmpty(request.game5())) {
            map.addAttribute("game5", Arrays.stream(request.game5().split(",")).map(Integer::parseInt).toList());
        }

        session.removeAttribute("dhPurchaseRequest");
        session.removeAttribute("dhPurchaseResponse");

        return "purchase/purchaseLottoResult";
    }

    /**
     * 예치금 입금 신청 post 처리 by selenium
     * @param session http session (for response message)
     * @return ajax response entity object
     */
    @ResponseBody
    @PostMapping("deposit")
    public ResponseEntity<?> deposit(HttpSession session) {
        //입금 신청을 위해 동행복권 로그인이 선행되어야 한다.
        //따라서, session attribute 에서 로그인 response dto 를 가져온다.
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        //로그인하지 않았거나, 로그인 실패로 정보가 없는 경우, 로그인 페이지로 보낸다.
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        //예치금 입금 신청 처리
        DhDepositResponse response = purchaseLotteryService.depositDhLottery(dhLoginResponse.id());

        //예치금 입금 신청 실패
        if (response == null) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        //정상 처리 결과를 담아서 반환한다.
        return ResponseEntity.ok().body(response);
    }

    /**
     * 예치금, 구매가능 게임수 갱신 post 처리 by selenium
     * @param session http session (for response message)
     * @return ajax response entity object
     */
    @ResponseBody
    @PostMapping("refresh")
    public ResponseEntity<?> refresh(HttpSession session) {
        //정보 갱신 시, 동행복권 로그인이 선행되어야 한다.
        //따라서, session attribute 에서 로그인 response dto 를 가져온다.
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        //로그인하지 않았거나, 로그인 실패로 정보가 없는 경우, 로그인 페이지로 보낸다.
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        //redis 에서 로그인 정보를 가져온다.
        String dhLotteryId = dhLoginResponse.id();
        DhLoginRequest request = redisTemplateService.getDhLoginInfo(dhLotteryId);

        //로그인 정보 만료
        if (request == null) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        //로그인 루틴에서 반환되는 DhLoginResponse 안에 예치금, 구매가능 게임이 들어있으므로, 이를 반환해주면 된다.
        DhLoginResponse response = purchaseLotteryService.loginDhLottery(request, true);
        session.setAttribute("dhLoginResponse", response);

        return ResponseEntity.ok().body(response);
    }

    /**
     * 로또 게임 생성 post 처리
     * @param request 로또 게임 생성 request dto
     * @return 로또 게임 생성 response dto
     */
    @ResponseBody
    @PostMapping("L645/game")
    public LottoGameResponse lottoGame(LottoGameRequest request) {
        Set<Integer> gameSet = new HashSet<>();

        //입력받은 번호를 모두 set add
        gameSet.add(request.number1());
        gameSet.add(request.number2());
        gameSet.add(request.number3());
        gameSet.add(request.number4());
        gameSet.add(request.number5());
        gameSet.add(request.number6());

        //NULL 값은 제거
        gameSet.remove(null);

        //랜덤 값 생성
        Random rnd = new Random();
        while (gameSet.size() < 6) {
            gameSet.add(rnd.nextInt(45) + 1); //set 이므로 중복값이 아닐때만 들어간다.
        }

        return LottoGameResponse.of(gameSet);
    }
}
