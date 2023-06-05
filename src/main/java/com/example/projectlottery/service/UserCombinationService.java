package com.example.projectlottery.service;

import com.example.projectlottery.domain.UserCombination;
import com.example.projectlottery.dto.request.UserCombinationRequest;
import com.example.projectlottery.dto.response.UserCombinationResponse;
import com.example.projectlottery.repository.UserCombinationRepository;
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
public class UserCombinationService {

    private final UserCombinationRepository userCombinationRepository;

    /**
     * 번호 조합 내역 저장
     * @param request 번호 조합 생성 내역
     */
    public void save(UserCombinationRequest request) {
        userCombinationRepository.save(request.toEntity());
    }


    /**
     * 로또추첨결과 scrap 후, 서비스 내 로또 번호조합 내역 등위 업데이트
     * @param drawNo 회차 번호
     * @param winNumber 당첨 번호 목록
     * @param bonusNumber 보너스 번호 목록
     */
    public void updateCombinationWin(Long drawNo, List<Integer> winNumber, Integer bonusNumber) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserCombination> userCombinations = userCombinationRepository.findByUserIdAndDrawNo(userId, drawNo);
        for (UserCombination u : userCombinations) {
            int rank = 0;
            int matched = 0;

            //당첨 번호와 일치하는 번호 개수를 계산
            if (winNumber.contains(u.getNumber1())) matched++;
            if (winNumber.contains(u.getNumber2())) matched++;
            if (winNumber.contains(u.getNumber3())) matched++;
            if (winNumber.contains(u.getNumber4())) matched++;
            if (winNumber.contains(u.getNumber5())) matched++;
            if (winNumber.contains(u.getNumber6())) matched++;

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
                    bonusNumber.equals(u.getNumber1()) ||
                            bonusNumber.equals(u.getNumber2()) ||
                            bonusNumber.equals(u.getNumber3()) ||
                            bonusNumber.equals(u.getNumber4()) ||
                            bonusNumber.equals(u.getNumber5()) ||
                            bonusNumber.equals(u.getNumber6()))) {
                //6개 번호 중에서 보너스 번호와 일치하는 번호가 존재하면, 등위를 하나 올려준다.
                rank = 2;
            }

            u.updateRank(rank);
        }
    }

    /**
     * 사용자의 번호 조합 생성 회차 목록 조회
     * @return 번호 조합 생성 회차 목록
     */
    @Transactional(readOnly = true)
    public List<Long> getCombinationDrawNo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userCombinationRepository.getCombinationDrawNo(userId);
    }

    /**
     * 회차별 번호 조합 내역 조회
     * @param drawNo 회차 번호
     * @return 번호 조합 내역
     */
    @Transactional(readOnly = true)
    public List<UserCombinationResponse> getUserCombination(Long drawNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return userCombinationRepository.findByUserIdAndDrawNo(userId, drawNo)
                .stream().map(UserCombinationResponse::from)
                .toList();
    }
}
