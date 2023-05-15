package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.PurchaseLotteryService;
import com.example.projectlottery.dto.request.DhLoginRequest;
import com.example.projectlottery.dto.request.DhLottoPurchaseRequest;
import com.example.projectlottery.dto.request.LottoGameRequest;
import com.example.projectlottery.dto.response.DhDepositResponse;
import com.example.projectlottery.dto.response.DhLoginResponse;
import com.example.projectlottery.dto.response.DhLottoPurchaseResponse;
import com.example.projectlottery.dto.response.LottoGameResponse;
import com.example.projectlottery.service.PurchaseService;
import com.example.projectlottery.service.RedisTemplateService;
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

@RequiredArgsConstructor
@RequestMapping("/purchase/dh")
@Controller
public class PurchaseController {

    private final RedisTemplateService redisTemplateService;

    private final PurchaseLotteryService purchaseLotteryService; //selenium processing service

    private final PurchaseService purchaseService; //backend service

    /**
     * 동행복권 로그인 form 표시
     */
    @GetMapping("login")
    public String dhLoginForm(HttpSession session, ModelMap map) {
        //session attribute 를 체크해서 alert 표시
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");
        if (dhLoginResponse != null && !dhLoginResponse.loginOk()) { //오류 메시지 처리
            map.addAttribute("dhLoginResponse", dhLoginResponse);
            session.removeAttribute("dhLoginResponse"); //오류를 출력했다면, 세션에서 지워준다.
        }

        return "purchase/dhLogin";
    }

    /**
     * 동행복권 로그인 post 처리
     * - 성공: 로또 구매 form
     * - 실패: 동행복권 사이트 로그인 form
     */
    @PostMapping("login")
    public String dhLogin(DhLoginRequest request, HttpSession session) {
        DhLoginResponse response = purchaseLotteryService.loginDhLottery(request, true);
        session.setAttribute("dhLoginResponse", response);

        if (response.loginOk()) {
            if (response.purchasableCount() == 0) {
                return "redirect:/purchase/dh/login";
            } else {
                return "redirect:/purchase/dh/L645";
            }
        } else {
            return "redirect:/purchase/dh/login";
        }
    }

    /**
     * 동행복권 로또 구매 form 표시
     */
    @GetMapping("L645")
    public String purchaseLottoForm(HttpSession session, ModelMap map) {
        //로그인 정보가 없으면, 다시 로그인페이지로 이동
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return "redirect:/purchase/dh/login";
        }

        //로그인 후, 예치금, 구매가능 정보를 표시하기 위해 model 에 넣어준다.
        map.addAttribute("dhLoginResponse", dhLoginResponse);

        //로그인 정보를 체크해서 정상 로그인 상태라면, 구매 페이지로 보낸다.
        //단, 구매 오류 시, 이 페이지로 오기때문에 구매 오류메세지를 담아서 보낸다.
        DhLottoPurchaseResponse dhPurchaseResponse = (DhLottoPurchaseResponse) session.getAttribute("dhPurchaseResponse");
        if (dhPurchaseResponse != null) {
            map.addAttribute("dhPurchaseResponse", dhPurchaseResponse);
        }

        return "purchase/purchaseLotto";
    }

    /**
     * 동행복권 로또 구매 post 처리
     */
    @PostMapping("L645")
    public String purchaseLotto(DhLottoPurchaseRequest request, HttpSession session) {
        //로그인 정보 오류 시, 로그인 페이지로 보낸다.
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");
        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return "redirect:/purchase/dh/login";
        }

        //구매 시, 한 번 더 로그인해서 구매처리해야하므로 session attribute 에서 로그인 response dto 를 가져와 redis 에서 로그인 정보를 가져온다.
        DhLottoPurchaseResponse response = purchaseLotteryService.purchaseDhL645(dhLoginResponse.id(), request);

        if (response == null) { //로그인정보 redis expired
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));
            return "redirect:/purchase/dh/login";
        }


        //구매성공시 session attribute 에 있던 로그인 response dto 삭제하고 구매정보 확인 페이지로 이동
        if (response.purchaseOk()) {
            session.removeAttribute("dhLoginResponse");

            return "redirect:/purchase/dh/history";
        }

        //구매실패시 원래페이지로 이동해서 오류메세지 표시
        session.setAttribute("dhPurchaseResponse", response);

        return "redirect:/purchase/dh/L645";
    }

    /**
     * 로또 구매 내역 조회 form
     */
    @GetMapping("history")
    public String purchaseHistory() {
        return "purchase/purchaseHistory";
    }

    /**
     * 예치금 입금 신청 처리 by selenium
     */
    @ResponseBody
    @PostMapping("deposit")
    public ResponseEntity<?> deposit(HttpSession session) {
        //입금 시, 한 번 더 로그인해서 입금신청해야하므로 session attribute 에서 로그인 response dto 를 가져와 redis 에서 로그인 정보를 가져온다.
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            //동행복권 로그인 미실시한 경우
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        DhDepositResponse response = purchaseLotteryService.depositDhLottery(dhLoginResponse.id());

        if (response == null) {
            //로그인정보가 expire 됨
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        return ResponseEntity.ok().body(response);
    }

    /**
     * 예치금, 구매가능 게임수 갱신 by selenium
     */
    @ResponseBody
    @PostMapping("refresh")
    public ResponseEntity<?> refresh(HttpSession session) {
        //새로고침 시, 한 번 더 로그인해야하므로 session attribute 에서 로그인 response dto 를 가져와 redis 에서 로그인 정보를 가져온다.
        DhLoginResponse dhLoginResponse = (DhLoginResponse) session.getAttribute("dhLoginResponse");

        if (dhLoginResponse == null || !dhLoginResponse.loginOk()) {
            //동행복권 로그인 미실시한 경우
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 정보가 없습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }


        String dhLotteryId = dhLoginResponse.id();
        DhLoginRequest request = redisTemplateService.getDhLoginInfo(dhLotteryId);

        if (request == null) { //로그인 정보 만료
            session.setAttribute("dhLoginResponse", DhLoginResponse.of(false, "로그인 세션이 만료되었습니다."));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/purchase/dh/login").build();
        }

        //로그인 루틴에서 반환되는 DhLoginResponse 안에 예치금, 구매가능 게임이 들어있으므로, 이를 반환해주면 된다.
        DhLoginResponse response = purchaseLotteryService.loginDhLottery(request, true);
        //세션 어트리뷰트에도 갱신해준다.
        session.setAttribute("dhLoginResponse", response);

        return ResponseEntity.ok().body(response);
    }

    /**
     * 로또 게임 생성
     */
    @ResponseBody
    @PostMapping("L645/game")
    public LottoGameResponse lottoGame(LottoGameRequest request) {
        return purchaseService.makeLottoGameSet(request);
    }
}
