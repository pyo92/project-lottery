package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.PurchaseLotteryService;
import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.request.DhLoginRequest;
import com.example.projectlottery.dto.request.DhLottoPurchaseRequest;
import com.example.projectlottery.dto.response.*;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.PurchaseResultService;
import com.example.projectlottery.service.RedisTemplateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/purchase/dh")
@Controller
public class PurchaseController {

    private final PurchaseLotteryService purchaseLotteryService;
    private final PurchaseResultService purchaseResultService;
    private final RedisTemplateService redisTemplateService;

    private final LottoService lottoService;

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
        String worker = SecurityContextHolder.getContext().getAuthentication().getName();
        redisTemplateService.savePurchaseWorkerInfo(worker);

        try {
            //동행복권 로그인 처리
            DhLoginResponse response = purchaseLotteryService.loginDhLottery(request);

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

        } catch (Exception ignored) {
            return "redirect:/purchase/dh/login";

        } finally {
            redisTemplateService.deletePurchaseWorkerInfo();
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

            //구매 내역을 server 에 저장한다.
            purchaseResultService.save(request.drawNo(), response);

            //구매 결과 페이지에 구매 회차와 성공여부를 보낸다.
            session.setAttribute("purchaseDrawNo", request.drawNo());

            return "redirect:/purchase/dh/L645/result";
        }

        //예치금 부족 등 구매 실패 시 구매 페이지로 다시 redirection
        session.setAttribute("dhPurchaseResponse", response);
        return "redirect:/purchase/dh/L645";
    }

    /**
     * 동행복권 로또 구매 결과 view
     * @param map thymeleaf binding model map
     * @return 로또 구매 결과 view file name
     */
    @GetMapping("L645/result")
    public String purchaseLottoResult(@RequestParam(required = false) Long drawNo, HttpSession session, ModelMap map) {
        //구매 완료 이후에 결과페이지로 넘어온 경우에 대한 처리
        Object purchaseDrawNo = session.getAttribute("purchaseDrawNo");
        if (purchaseDrawNo != null) {
            drawNo = (Long) purchaseDrawNo; //구매 회차로 변경한다.
            map.addAttribute("purchaseAlert", "show"); //구매 완료 alert 표시 여부 binding
            session.removeAttribute("purchaseDrawNo");
        }

        //사용자의 구매 회차 목록 전체를 조회한다.
        List<Long> purchasedDrawNo = purchaseResultService.getPurchasedDrawNo();

        if (purchasedDrawNo.size() == 0) { //구매 내역 X
            //구매 내역이 존재하지 않으면, 오류 alert 표시
            map.addAttribute("purchasedNoneAlert", "show");

            map.addAttribute("drawNos", purchasedDrawNo);

            //회차를 표시하기 위해 회차 정보 model binding
            map.addAttribute("drawNo", null);

            //구매 게임 내역 model binding
            map.addAttribute("purchaseResult", List.of());

        } else { //구매 내역 O
            //회차 예외처리
            Long latestDrawNo = purchasedDrawNo.get(0);
            if (drawNo == null || !purchasedDrawNo.contains(drawNo)) {
                return "redirect:/purchase/dh/L645/result?drawNo=" + latestDrawNo;
            }

            List<PurchaseResultResponse> purchaseResult = purchaseResultService.getPurchaseResult(drawNo);

            //회차 select 표시하기 위해 최신 회차 model binding
            map.addAttribute("drawNos", purchasedDrawNo);

            //회차를 표시하기 위해 회차 정보 model binding
            map.addAttribute("drawNo", drawNo);

            //구매 게임 내역 model binding
            map.addAttribute("purchaseResult", purchaseResult);

            //당첨번호와 일치하면 색상을 표시하기 위한 회차별 당첨번호 내역 model binding
            LottoDto lotto = lottoService.getLotto(drawNo);
            List<Integer> winNumbers = List.of(lotto.number1(), lotto.number2(), lotto.number3(), lotto.number4(), lotto.number5(), lotto.number6());
            map.addAttribute("winNumbers", winNumbers);
        }

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
        DhLoginResponse response = purchaseLotteryService.loginDhLottery(request);
        session.setAttribute("dhLoginResponse", response);

        return ResponseEntity.ok().body(response);
    }
}
