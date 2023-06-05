package com.example.projectlottery.service;

import com.example.projectlottery.domain.PurchaseResult;
import com.example.projectlottery.dto.request.PurchaseResultRequest;
import com.example.projectlottery.dto.response.DhLottoPurchaseResponse;
import com.example.projectlottery.dto.response.PurchaseResultResponse;
import com.example.projectlottery.repository.PurchaseResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PurchaseResultService {

    private final PurchaseResultRepository purchaseResultRepository;

    /**
     * 구매 내역 저장
     * @param dto 구매 내역 request dto
     */
    public void save(Long drawNo, DhLottoPurchaseResponse dto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //DhLottoPurchaseResponse 구매결과를 구매 내역 저장용 dto 로 파싱
        for (String mapKey : dto.purchaseTypes().keySet()) {
            PurchaseResultRequest request = PurchaseResultRequest.of(
                    userId,
                    drawNo,
                    dto.purchaseTypes().get(mapKey),
                    dto.games().get(mapKey).get(0),
                    dto.games().get(mapKey).get(1),
                    dto.games().get(mapKey).get(2),
                    dto.games().get(mapKey).get(3),
                    dto.games().get(mapKey).get(4),
                    dto.games().get(mapKey).get(5)
            );

            purchaseResultRepository.save(request.toEntity());
        }
    }

    /**
     * 로또추첨결과 scrap 후, 서비스 내 로또 구매내역 등위 업데이트
     * @param drawNo 회차 번호
     * @param winNumber 당첨 번호 목록
     * @param bonusNumber 보너스 번호 목록
     */
    public void updatePurchasedWin(Long drawNo, List<Integer> winNumber, Integer bonusNumber) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<PurchaseResult> purchaseResults = purchaseResultRepository.findByUserIdAndDrawNo(userId, drawNo);
        for (PurchaseResult r : purchaseResults) {
            int rank = 0;
            int matched = 0;

            //당첨 번호와 일치하는 번호 개수를 계산
            if (winNumber.contains(r.getNumber1())) matched++;
            if (winNumber.contains(r.getNumber2())) matched++;
            if (winNumber.contains(r.getNumber3())) matched++;
            if (winNumber.contains(r.getNumber4())) matched++;
            if (winNumber.contains(r.getNumber5())) matched++;
            if (winNumber.contains(r.getNumber6())) matched++;

            if (matched == 3) {
                rank = 5;
            } else if (matched == 4) {
                rank = 4;
            } else if (matched == 5) {
                rank = 3;
            } else if (matched == 6) {
                rank = 1;
            }

            //5개 일치일 때만, 보너스 번호를 검증하면 된다. (2등 혹은 3등이므로)
            if (matched == 5 && (
                    bonusNumber.equals(r.getNumber1()) ||
                    bonusNumber.equals(r.getNumber2()) ||
                    bonusNumber.equals(r.getNumber3()) ||
                    bonusNumber.equals(r.getNumber4()) ||
                    bonusNumber.equals(r.getNumber5()) ||
                    bonusNumber.equals(r.getNumber6()))) {
                //6개 번호 중에서 보너스 번호와 일치하는 번호가 존재하면, 등위를 하나 올려준다.
                rank = 2;
            }

            r.updateRank(rank);
        }
    }

    /**
     * 사용자의 구매 회차 목록 조회
     * @return 구매 회차 목록
     */
    @Transactional(readOnly = true)
    public List<Long> getPurchasedDrawNo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return purchaseResultRepository.getPurchasedDrawNo(userId);
    }

    /**
     * 회차별 구매 내역 조회
     * @param drawNo 회차
     * @return 구매 내역
     */
    @Transactional(readOnly = true)
    public List<PurchaseResultResponse> getPurchaseResult(Long drawNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return purchaseResultRepository.findByUserIdAndDrawNo(userId, drawNo)
                .stream().map(PurchaseResultResponse::from)
                .toList();
    }
}
