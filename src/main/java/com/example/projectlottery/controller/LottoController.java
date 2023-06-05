package com.example.projectlottery.controller;

import com.example.projectlottery.dto.request.LottoGameNegativeRequest;
import com.example.projectlottery.dto.request.LottoGameRequest;
import com.example.projectlottery.dto.request.UserCombinationRequest;
import com.example.projectlottery.dto.response.LottoGameResponse;
import com.example.projectlottery.dto.response.LottoResponse;
import com.example.projectlottery.dto.response.UserCombinationResponse;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.UserCombinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@RequestMapping("/L645")
@Controller
public class LottoController {

    private final LottoService lottoService;

    private final UserCombinationService userCombinationService;

    /**
     * 로또추첨결과 view
     * @param drawNo 회차번호
     * @param map thymeleaf binding model map
     * @return 로또추첨결과 view file name
     */
    @GetMapping
    public String lotto(@RequestParam(required = false) Long drawNo, ModelMap map) {
        //회차 예외처리
        Long latestDrawNo = lottoService.getLatestDrawNo();
        if (drawNo == null || drawNo > latestDrawNo || drawNo < 1) {
            return "redirect:/L645?drawNo=" + latestDrawNo;
        }

        //회차 선택 selector value list
        List<Long> drawNos = LongStream.range(1, latestDrawNo + 1)
                .boxed()
                .sorted(Comparator.reverseOrder()).toList();

        //추첨결과 response dto
        LottoResponse lottoResponse = lottoService.getLottoResponse(drawNo);

        //model map binding
        map.addAttribute("latestDrawNo", latestDrawNo);
        map.addAttribute("drawNos", drawNos);
        map.addAttribute("lottoResponse", lottoResponse);

        return "lotto/drawResult";
    }

    /**
     * 로또도구 (번호조합기) view
     * @return 로또 도구 view file name
     */
    @GetMapping("/tool")
    public String lottoTool() {
        return "lotto/lottoTool";
    }

    /**
     * 로또도구 (번호조합기) 결과 view
     * @param drawNo 회차 번호
     * @param map thymeleaf binding model map
     * @return 로또 도구 번호 조합 결과 view file name
     */
    @GetMapping("tool/result")
    public String lottoToolResult(@RequestParam(required = false) Long drawNo, ModelMap map) {
        //사용자의 번호 조합 생성 회차 목록 전체를 조회한다.
        List<Long> combinationDrawNo = userCombinationService.getCombinationDrawNo();

        if (combinationDrawNo.size() == 0) { //조합 내역 X
            //번호 조합 내역이 존재하지 않으면, 오류 alert 표시
            map.addAttribute("combinationNoneAlert", "show");

            map.addAttribute("drawNos", combinationDrawNo);

            //회차를 표시하기 위해 회차 정보 model binding
            map.addAttribute("drawNo", null);

            //번호 조합 내역 model binding
            map.addAttribute("combinationResult", List.of());
        } else { //조합 내역 O
            //회차 예외처리
            Long latestDrawNo = combinationDrawNo.get(0);
            if (drawNo == null || !combinationDrawNo.contains(drawNo)) {
                return "redirect:/L645/tool/result?drawNo=" + latestDrawNo;
            }

            List<UserCombinationResponse> combinationResult = userCombinationService.getUserCombination(drawNo);

            //회차 select 표시하기 위해 최신 회차 model binding
            map.addAttribute("drawNos", combinationDrawNo);

            //회차를 표시하기 위해 회차 정보 model binding
            map.addAttribute("drawNo", drawNo);

            //구매 게임 내역 model binding
            map.addAttribute("combinationResult", combinationResult);
        }

        return "lotto/userCombinationResult";
    }

    /**
     * 로또분석 view
     * @param map thymeleaf binding model map
     * @return 로또분석 view file name
     */
    @GetMapping("/analysis")
    public String lottoAnalysis(ModelMap map) {
        Long latestDrawNo = lottoService.getLatestDrawNo();
        map.addAttribute("latestDrawNo", latestDrawNo);

        return "lotto/lottoAnalysis";
    }

    /**
     * 로또 게임 생성 post 처리
     * @param request 로또 게임 생성 request dto
     * @return 로또 게임 생성 response dto
     */
    @ResponseBody
    @PostMapping("/game")
    public LottoGameResponse lottoGame(LottoGameRequest request, Long drawNo, String combinationType) {
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
        long seed = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName().replace("k_", ""));
        while (gameSet.size() < 6) {
            rnd.setSeed(System.nanoTime() ^ seed);
            gameSet.add(rnd.nextInt(45) + 1); //set 이므로 중복값이 아닐때만 들어간다.
        }

        //번호조합 내역 저장
        List<Integer> numbers = new ArrayList<>(gameSet);
        numbers.sort(Integer::compareTo);
        userCombinationService.save(UserCombinationRequest.of(
                drawNo,
                combinationType,
                numbers.get(0),
                numbers.get(1),
                numbers.get(2),
                numbers.get(3),
                numbers.get(4),
                numbers.get(5)
        ));

        return LottoGameResponse.of(gameSet);
    }

    /**
     * 제외수 로또 게임 생성 post 처리
     * @param request 제외수 로또 게임 생성 request dto
     * @return 제외수 로또 게임 생성 response dto
     */
    @ResponseBody
    @PostMapping("/game/negative")
    public LottoGameResponse lottoGame(LottoGameNegativeRequest request, Long drawNo, String combinationType) {
        Set<Integer> gameSet = new HashSet<>();

        //제외수 전체를 set 으로 생성
        Set<Integer> negativeSet = new HashSet<>(request.toList());

        //랜덤 값 생성
        Random rnd = new Random();
        long seed = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName().replace("k_", ""));
        while (gameSet.size() < 6) {
            rnd.setSeed(System.nanoTime() ^ seed);
            int r = rnd.nextInt(45) + 1;
            if (negativeSet.contains(r)) { //제외수에 해당하는 숫자면, continue
                continue;
            }
            gameSet.add(r); //set 이므로 중복값이 아닐때만 들어간다.
        }

        //번호조합 내역 저장
        List<Integer> numbers = new ArrayList<>(gameSet);
        numbers.sort(Integer::compareTo);
        userCombinationService.save(UserCombinationRequest.of(
                drawNo,
                combinationType,
                numbers.get(0),
                numbers.get(1),
                numbers.get(2),
                numbers.get(3),
                numbers.get(4),
                numbers.get(5)
        ));

        return LottoGameResponse.of(gameSet);
    }
}
